package com.example.geoxplore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.Avatar;
import com.example.geoxplore.api.model.UserStatistics;
import com.example.geoxplore.api.service.UserService;
import com.example.geoxplore.utils.SavedData;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

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
    @BindView(R.id.user_profile_image)
    ImageView mUserImage;
    @BindView(R.id.user_profile_pts)
    TextView mUserPtsText;
    @BindView(R.id.user_profile_box1)
    TextView mUserBox1;
    @BindView(R.id.user_profile_box2)
    TextView mUserBox2;
    @BindView(R.id.user_profile_box3)
    TextView mUserBox3;
    @BindView(R.id.user_profile_box4)
    TextView mUserBox4;
    @BindView(R.id.user_profile_title)
    TextView mUserTitle;


    Typeface mainFont;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        mainFont = Typeface.createFromAsset(getActivity().getAssets(), "main.ttf");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initial();
        loadData();

    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        ApiUtils.getService(UserService.class)
                .getUserStats(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x -> null)
                .subscribe(userStatistics -> {
                    if (userStatistics != null) {
                        mExpProgressBar.setProgress(100 - (int) userStatistics.getToNextLevel());
                        mUserLvlText.setText("level " + userStatistics.getLevel());
                        mBoxesText.setText(String.valueOf(userStatistics.getOpenedOverallChests()));
                        mFriendsText.setText(String.valueOf(userStatistics.getFriends()));
                        mUserNameText.setText(userStatistics.getUsername());
                        mUserPercentToNextLvlText.setText((int) userStatistics.getToNextLevel() + "% to next");
                        mUserPtsText.setText(String.valueOf(userStatistics.getExperience()));
                        mUserBox1.setText("x" + userStatistics.getChestStats().getOpenedOverallCommonChests());
                        mUserBox2.setText("x" + userStatistics.getChestStats().getOpenedOverallRareChests());
                        mUserBox3.setText("x" + userStatistics.getChestStats().getOpenedOverallEpicChests());
                        mUserBox4.setText("x" + userStatistics.getChestStats().getOpenedOverallLegendaryChests());
                        mUserTitle.setText(getTitle(userStatistics.getLevel()));


                        SavedData.saveUserLevel(getContext(), userStatistics.getLevel());
                    }
                });


        ApiUtils
                .getService(UserService.class)
                .getAvatar(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x -> {
                    Toast.makeText(getContext(), x.getMessage(), Toast.LENGTH_LONG).show();

                    return null;
                })
                .subscribe(bodyResponse -> {
                    if (bodyResponse.isSuccessful()) {
                        if (bodyResponse.body() != null) {

                            Bitmap bm = BitmapFactory.decodeStream(bodyResponse.body().byteStream());
                            mUserImage.setImageBitmap(bm);
                        }

                    }


                });
    }


    private String getTitle(int level){
        if (level > 100)
            return "Grandmaster";
        else if (level > 60)
            return "Master";
        else if (level > 30)
            return "Advanced";
        else if (level > 10)
            return "Intermediate";
        else
            return "Beginner";
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        File f = new File(getContext().getCacheDir(), "avatar");

        if (resultCode == RESULT_OK) try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
            if (/*file_size<5*/true) {
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", f.getName(), RequestBody.create(MediaType.parse("image/*"), f));

                ApiUtils.getService(UserService.class)
                        .setAvatar(getArguments().getString(Intent.EXTRA_USER), filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(voidResponse -> {
                            if (voidResponse.isSuccessful()) {
                            } else {
                            }
                        });

                mUserImage.setImageBitmap(selectedImage);
            } else {
                Toast.makeText(getContext(), "Size must be beneath 5kb", Toast.LENGTH_LONG).show();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        else {
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }


    }


    private void initial() {
        mUserImage.setImageResource(R.drawable.user);
        mExpProgressBar.setMax(100);

        mUserNameText.setTypeface(mainFont);
        mBadgesText.setTypeface(mainFont);
        mFriendsText.setTypeface(mainFont);
        mBoxesText.setTypeface(mainFont);
        mUserLvlText.setTypeface(mainFont);
        mUserPercentToNextLvlText.setTypeface(mainFont);
        mUserPtsText.setTypeface(mainFont);
        mUserBox1.setTypeface(mainFont);
        mUserBox2.setTypeface(mainFont);
        mUserBox3.setTypeface(mainFont);
        mUserBox4.setTypeface(mainFont);
        mUserTitle.setTypeface(mainFont);

        mBadgesText.setText("0");

        mUserPercentToNextLvlText.setTypeface(null, Typeface.BOLD);
        mUserLvlText.setTypeface(null, Typeface.BOLD);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
