package com.bbilandzi.diplomskiandroidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;

public class AuthUtils {
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_JWT = "jwt_token";

    public static boolean isAuthenticated(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT, null);

        if (token == null || token.isEmpty()) return false;

        long exp = Long.parseLong(extractClaim(getToken(context),"exp"));

        return Instant.ofEpochSecond(exp).toEpochMilli()>Instant.now().toEpochMilli();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT, null);
        return token;
    }

    public static Long getUserId(Context context) {
        return Long.parseLong(extractClaim(getToken(context), "USER_ID"));
    }

    public static String getUsername(Context context) {
        return extractClaim(getToken(context), "sub");
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

    public static String extractClaim(String token, String claimKey) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedString = new String(decodedBytes, "UTF-8");

            return getClaimFromJson(decodedString, claimKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getClaimFromJson(String json, String claimKey) {
        String search = "\"" + claimKey + "\":";
        int startIndex = json.indexOf(search);
        if (startIndex == -1) {
            return null;
        }
        startIndex += search.length();
        int endIndex;

        if (json.charAt(startIndex) == '\"') {
            startIndex++;
            endIndex = json.indexOf("\"", startIndex);
        } else {
            endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
        }

        if (endIndex == -1) {
            return null;
        }
        return json.substring(startIndex, endIndex);
    }

}
