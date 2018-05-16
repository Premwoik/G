package com.example.geoxplore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.UserStatistics;
import com.example.geoxplore.api.service.UserService;
import com.example.geoxplore.utils.SavedData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//TODO no zrobic, zeby jakos ladniej wygladało TJ usunąć niedziałające pola
public class UserProfileFragment extends Fragment {
    public static final String TAG = "user_profile_fragment";
    private Unbinder unbinder;

    @BindView(R.id.user_profile_name)
    TextView mUserNameText;
    @BindView(R.id.user_profile_friends)
    TextView mFriendsText;
    @BindView(R.id.user_profile_boxes)
    TextView mBoxesText;
    @BindView(R.id.user_profile_badges)
    TextView mBadgesText;
    @BindView(R.id.user_profile_exp_progress_bar)
    ProgressBar mExpProgressBar;
    @BindView(R.id.user_profile_current_lvl)
    TextView mUserLvlText;
    @BindView(R.id.user_profile_percent_to_next_lvl)
    TextView mUserPercentToNextLvlText;
    @BindView(R.id.user_profile_progress_bar3)
    ProgressBar mProgress3;
    @BindView(R.id.user_profile_progress_bar2)
    ProgressBar mProgress2;
    @BindView(R.id.user_profile_progress_bar1)
    ProgressBar mProgress1;
    @BindView(R.id.user_profile_image)
    ImageView mUserImage;
    @BindView(R.id.user_profile_pts)
    TextView mUserPtsText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initial();
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void loadData(){
        ApiUtils.getService(UserService.class)
                .getUserStats(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x-> new UserStatistics("error",0,0,0,0))
                .subscribe(userStatistics -> {
                    mExpProgressBar.setProgress(100 - (int) userStatistics.getToNextLevel());
                    mUserLvlText.setText("level " + userStatistics.getLevel());
                    mBoxesText.setText(String.valueOf(userStatistics.getOpenedChests()));
                    mUserNameText.setText(userStatistics.getUsername());
                    mUserPercentToNextLvlText.setText((int) userStatistics.getToNextLevel() + "% to next");
                    SavedData.saveUserLevel(getContext(), userStatistics.getLevel());
                });
    }

    private void initial(){
        SpannableString ss1=  new SpannableString("38pts");
        ss1.setSpan(new RelativeSizeSpan(3f), 0,2, 0); // set size
        mUserPtsText.setText(ss1);
        mFriendsText.setText("23");
        mBadgesText.setText("12");
        mUserImage.setImageResource(R.drawable.user);
        mExpProgressBar.setMax(100);

        mUserPercentToNextLvlText.setTypeface(null, Typeface.BOLD);
        mUserLvlText.setTypeface(null, Typeface.BOLD);

        mProgress1.setMax(100);
        mProgress1.setProgress(30);
        mProgress2.setMax(100);
        mProgress2.setProgress(80);
        mProgress3.setMax(100);
        mProgress3.setProgress(60);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
