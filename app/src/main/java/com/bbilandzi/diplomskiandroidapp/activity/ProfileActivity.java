package com.bbilandzi.diplomskiandroidapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.utils.NotificationHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileActivity extends BaseActivity{
    private ImageView profileImage;
    private TextView userName;
    private Button logoutButton;
    private Button notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        logoutButton = findViewById(R.id.logout_button);

        notif = findViewById(R.id.notif);
        logoutButton.setOnClickListener(v -> logout());
        userName.setText(AuthUtils.getUsername(this));
        notif.setOnClickListener(v->{
            NotificationHelper notificationHelper = new NotificationHelper();
            notificationHelper.createNotification(ProfileActivity.this);
        });
    }

    @Override
    protected void onClose(Bundle savedInstanceState) {}

    private void logout() {
        AuthUtils.clearToken(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
