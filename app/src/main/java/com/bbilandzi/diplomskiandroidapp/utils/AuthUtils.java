package com.bbilandzi.diplomskiandroidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthUtils {
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_JWT = "jwt_token";

    public static boolean isAuthenticated(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT, null);

        // Add actual JWT validation logic here, such as checking expiration
        return token != null && !token.isEmpty();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT, null);
        return token;
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_JWT, token);
        editor.apply();
    }

    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_JWT);
        editor.apply();
    }
}
