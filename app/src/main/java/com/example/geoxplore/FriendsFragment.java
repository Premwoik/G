package com.example.geoxplore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.service.UserService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FriendsFragment extends Fragment {

    public static final String TAG = "friends";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private Unbinder unbinder;

    @BindView(R.id.add_friend_name)
    EditText mNameFriendToAdd;


    @OnClick(R.id.add_friend_btn)
    public void addFriendBtn(Button btn){
        String friendName = mNameFriendToAdd.getText().toString();
        ApiUtils
                .getService(UserService.class)
                .addFriend(getArguments().getString(Intent.EXTRA_USER), friendName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voidResponse -> {
                    if (voidResponse.code() == 409) {
                        Toast.makeText(getContext(), "Already added", Toast.LENGTH_SHORT).show();
                    }
                    else if(voidResponse.code() == 200) {
                        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(this).attach(this).commit();
                    }
                    else{
                        Toast.makeText(getContext(), "error code: " + voidResponse.errorBody().string(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public FriendsFragment() {
    }

    @SuppressWarnings("unused")
    public static FriendsFragment newInstance(int columnCount) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.fragment_friend_list, container, false);

        unbinder = ButterKnife.bind(this, mview);
        View view = mview.findViewById(R.id.friends_list);


        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            ApiUtils
                    .getService(UserService.class)
                    .getFriends(getArguments().getString(Intent.EXTRA_USER))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(x -> new ArrayList<>())
                    .subscribe(x -> {
                        recyclerView.setAdapter(new MyFriendRecyclerViewAdapter(x, getArguments().getString(Intent.EXTRA_USER)));
                    });
        }
        return mview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
