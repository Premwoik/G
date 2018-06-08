package com.example.geoxplore.api.service;

import com.example.geoxplore.api.model.Avatar;
import com.example.geoxplore.api.model.Chest;
import com.example.geoxplore.api.model.HomeCords;
import com.example.geoxplore.api.model.OpenBoxResponseData;
import com.example.geoxplore.api.model.SecurityToken;
import com.example.geoxplore.api.model.UserCredentials;
import com.example.geoxplore.api.model.UserRegistrationForm;
import com.example.geoxplore.api.model.UserStatistics;
import com.example.geoxplore.api.model.UserStatsRanking;

import java.io.File;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UserService {

    @POST("/user-management/create-user")
    Observable<Response<Void>> register(@Body UserRegistrationForm registrationForm);

    @POST("/login")
    Observable<SecurityToken> login(@Body UserCredentials credentials);

    @GET("/user/my-statistics")
    Observable<UserStatistics> getUserStats(@Header("Authorization") String token);


    @GET("/community/avatar/{username}")
    Observable<Response<ResponseBody>> getUserAvatar(@Header("Authorization") String token, @Path("username") String username);

    @Streaming
    @GET("/user/avatar")
    Observable<Response<ResponseBody>> getAvatar(@Header("Authorization") String token);

    @Multipart
    @POST("/user/avatar")
    Observable<Response<Void>> setAvatar(@Header("Authorization") String token, @Part MultipartBody.Part image);

    @POST("/user/set-home")
    Observable<Response<Void>> setHome(@Header("Authorization") String token, @Body HomeCords cords);

    @GET("/user/get-home")
    Observable<HomeCords> getHome(@Header("Authorization") String token);

    @GET("/user/chests")
    Observable<List<Chest>> getChests(@Header("Authorization") String token);

    @GET("/community/ranking")
    Observable<List<UserStatsRanking>> getRanking(@Header("Authorization") String token);

    @POST("/user/open-chest/{id}")
    Observable<OpenBoxResponseData> openChest(@Header("Authorization") String token, @Path("id") long id);




    //ONLY FOR TESTS
    @GET("/user-management/list-users")
    Observable<ResponseBody> getUsers(@Header("Authorization") String token);
    // metoda do testów. losuje na nowo paramety usera(exp itd)
    @GET("/user-management/reroll")
    Observable<Response<Void>> makeReroll(@Header("Authorization") String token);
}