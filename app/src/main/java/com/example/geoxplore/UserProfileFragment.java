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

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
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
//                .onErrorReturn(x-> new UserStatistics("error",0,0,0,0))
                .subscribe(userStatistics -> {
                    mExpProgressBar.setProgress(100 - (int) userStatistics.getToNextLevel());
                    mUserLvlText.setText("level " + userStatistics.getLevel());
                    mBoxesText.setText(String.valueOf(userStatistics.getChestStats().getOpenedOverallChests()));
                    mUserNameText.setText(userStatistics.getUsername());
                    mUserPercentToNextLvlText.setText((int) userStatistics.getToNextLevel() + "% to next");
                    SavedData.saveUserLevel(getContext(), userStatistics.getLevel());
                });



        ApiUtils
                .getService(UserService.class)
                .getAvatar(getArguments().getString(Intent.EXTRA_USER))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x->{
                    Toast.makeText(getContext(), x.getMessage(), Toast.LENGTH_LONG).show();

                    return null;
                })
                .subscribe(bodyResponse -> {

                    Toast.makeText(getContext(), String.valueOf(bodyResponse.code()), Toast.LENGTH_LONG).show();
                    if(bodyResponse.isSuccessful()){
                        if(bodyResponse.body()!=null){

                            Bitmap bm = BitmapFactory.decodeStream(bodyResponse.body().byteStream());
                            mUserImage.setImageBitmap(bm);
                        }

                    }



                });
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



//Convert bitmap to byte array

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            int file_size = Integer.parseInt(String.valueOf(f.length()/1024));
            if(/*file_size<5*/true){
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", f.getName(), RequestBody.create(MediaType.parse("image/*"), f));

                ApiUtils.getService(UserService.class)
                        .setAvatar(getArguments().getString(Intent.EXTRA_USER), filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(voidResponse -> {
                            if (voidResponse.isSuccessful()) {
                                Toast.makeText(getContext(), "Good", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), String.valueOf(voidResponse.code()+voidResponse.message()), Toast.LENGTH_LONG).show();
                            }
                        });

                mUserImage.setImageBitmap(selectedImage);
            }else{
                Toast.makeText(getContext(), "Size must be beneath 5kb", Toast.LENGTH_LONG).show();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }


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
