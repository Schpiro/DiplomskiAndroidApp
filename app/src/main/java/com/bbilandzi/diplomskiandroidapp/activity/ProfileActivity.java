package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileActivity extends BaseActivity{
    private ImageView profileImage;
    private TextView userName;
    private Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(v -> logout());
        userName.setText(AuthUtils.getUsername(this));

        Button startCallButton = findViewById(R.id.button_start_call);
        startCallButton.setOnClickListener(v -> {
            // Start the VideoCallActivity
            Intent intent = new Intent(ProfileActivity.this, VideoCallActivity.class);
            startActivity(intent);
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
