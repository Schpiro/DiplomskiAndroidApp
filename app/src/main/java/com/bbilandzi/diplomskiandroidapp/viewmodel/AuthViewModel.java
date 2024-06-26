package com.bbilandzi.diplomskiandroidapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.activity.AuthActivity;
import com.bbilandzi.diplomskiandroidapp.model.AuthRequest;
import com.bbilandzi.diplomskiandroidapp.model.AuthResponse;
import com.bbilandzi.diplomskiandroidapp.repository.AuthRepository;
import com.bbilandzi.diplomskiandroidapp.utils.AuthCallback;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;


import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(Context context, AuthRequest authRequest, AuthCallback callback) {
        authRepository.login(authRequest)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getJwt();
                            Log.d("Login Success", "JWT Token: " + token);
                            AuthUtils.saveToken(context, token);
                            callback.onAuthSuccess(token);
                        } else {
                            Log.e("Login Error", "Failed to authenticate user");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                        Log.e("Login Error", "Failed to connect to authentication server: " + throwable.getMessage());
                        callback.onAuthError("Failed to authenticate user"); // Notify error
                    }
                });
    }

    public void register(Context context, AuthRequest authRequest, AuthCallback callback) {
        authRepository.register(authRequest)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getJwt();
                            Log.d("Registration Success", "JWT Token: " + token);
                            AuthUtils.saveToken(context, token);
                            callback.onAuthSuccess(token);
                        } else {
                            Log.e("Registration Error", "Failed to authenticate user");
                            callback.onAuthError("Failed to authenticate user"); // Notify error
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                        Log.e("Registration Error", "Failed to connect to authentication server: " + throwable.getMessage());

                    }
                });
    }
}
