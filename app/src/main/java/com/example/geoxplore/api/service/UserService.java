package com.example.geoxplore.api.service;

import com.example.geoxplore.api.model.Chest;
import com.example.geoxplore.api.model.HomeCords;
import com.example.geoxplore.api.model.SecurityToken;
import com.example.geoxplore.api.model.UserCredentials;
import com.example.geoxplore.api.model.UserRegistrationForm;
import com.example.geoxplore.api.model.UserStatistics;
import com.example.geoxplore.api.model.UserStatsRanking;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @POST("/user-management/create-user")
    Observable<Response<Void>> register(@Body UserRegistrationForm registrationForm);

    @POST("/login")
    Observable<SecurityToken> login(@Body UserCredentials credentials);

    @GET("/user-management/my-statistics")
    Observable<UserStatistics> getUserStats(@Header("Authorization") String token);

    @POST("/user-management/set-home")
    Observable<Response<Void>> setHome(@Header("Authorization") String token, @Body HomeCords cords);

    @GET("/user-management/chests")
    Observable<List<Chest>> getChests(@Header("Authorization") String token);

    @GET("/user-management/ranking")
    Observable<List<UserStatsRanking>> getRanking(@Header("Authorization") String token);


    //ONLY FOR TESTS
    @GET("/user-management/list-users")
    Observable<ResponseBody> getUsers(@Header("Authorization") String token);
    // TODO metoda do testów. losuje na nowo paramety usera(exp itd)
    @GET("/user-management/reroll")
    Observable<Response<Void>> makeReroll(@Header("Authorization") String token);
}