package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.AuthRequest;
import com.bbilandzi.diplomskiandroidapp.model.LoginDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("authentication/login")
    Call<LoginDTO> loginUser(@Body AuthRequest authRequest);

    @POST("authentication/register")
    Call<LoginDTO> registerUser(@Body AuthRequest authRequest);
}
