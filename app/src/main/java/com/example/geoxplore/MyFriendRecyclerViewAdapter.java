package com.example.geoxplore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.Friend;
import com.example.geoxplore.api.service.UserService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MyFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<Friend> friends;
    String token;



    public MyFriendRecyclerViewAdapter(List<Friend> friends, String token) {
        this.friends = friends;
        this.token = token;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (position > 0) {

            holder.mItem = getItem(position);
            holder.mLvl.setText(String.valueOf(getItem(position).getLevel()));
            holder.mName.setText(getItem(position).getUsername());
            holder.mChests.setText(String.valueOf(getItem(position).getOpenedChests()));
            holder.mAvatar.setVisibility(View.VISIBLE);


            ApiUtils
                    .getService(UserService.class)
                    .getUserAvatar(token, getItem(position).getUsername())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(x->{
                        return null;
                    })
                    .subscribe(bodyResponse -> {
                        if(bodyResponse.isSuccessful()){
                            if(bodyResponse.body()!=null){
                                Bitmap bm = BitmapFactory.decodeStream(bodyResponse.body().byteStream());
                                holder.mAvatar.setImageBitmap(bm);
                            }
                        }
                    });
        }
        else{
            holder.mAvatar.setVisibility(View.INVISIBLE);
            holder.mItem = null;
            holder.mLvl.setText("Level:");
            holder.mName.setText("Username:");
            holder.mChests.setText("Chests:");
        }


        holder.mView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return friends.size() + 1;
    }

    private Friend getItem(int position) {
        return friends.get(position - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLvl;
        public final TextView mName;
        public final TextView mChests;
        public final CircleImageView mAvatar;
        public Friend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLvl = (TextView) view.findViewById(R.id.friend_lvl);
            mName = (TextView) view.findViewById(R.id.friend_name);
            mChests = (TextView) view.findViewById(R.id.friend_chests);
            mAvatar = (CircleImageView) view.findViewById(R.id.friend_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
