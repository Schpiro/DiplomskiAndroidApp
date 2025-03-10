package com.bbilandzi.diplomskiandroidapp.repository;


import com.bbilandzi.diplomskiandroidapp.model.AuthRequest;
import com.bbilandzi.diplomskiandroidapp.model.LoginDTO;
import com.bbilandzi.diplomskiandroidapp.service.AuthService;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;

public class AuthRepository {

    AuthService authService;

    @Inject
    public AuthRepository(Retrofit client) {
        this.authService = client.create(AuthService.class);
    }

    public Call<LoginDTO> login(AuthRequest authRequest) {
        return authService.loginUser(authRequest);
    }

    public Call<LoginDTO> register(AuthRequest authRequest) {
        return authService.registerUser(authRequest);
    }
}
