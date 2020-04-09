package com.example.attendance.network;

import com.example.attendance.models.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/api_login")
    Observable<UserModel> authenticateUser(@Body UserModel user);
}
