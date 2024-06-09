package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.LoginRequest;
import com.bbilandzi.diplomskiandroidapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("authentication/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
