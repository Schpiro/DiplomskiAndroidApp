package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.AuthRequest;
import com.bbilandzi.diplomskiandroidapp.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("authentication/login")
    Call<AuthResponse> loginUser(@Body AuthRequest authRequest);

    @POST("authentication/register")
    Call<AuthResponse> registerUser(@Body AuthRequest authRequest);
}
