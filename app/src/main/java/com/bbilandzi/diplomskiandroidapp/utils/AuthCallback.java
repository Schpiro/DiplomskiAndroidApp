package com.bbilandzi.diplomskiandroidapp.utils;

public interface AuthCallback {
    void onAuthSuccess(String token);
    void onAuthError(String errorMessage);
}
