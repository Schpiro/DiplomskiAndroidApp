package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;

public class MainActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AuthUtils.isAuthenticated(this)) {
            navigateTo(ContactsActivity.class);
        } else {
            navigateTo(AuthActivity.class);
        }

        this.webSocketManager = new WebSocketManager();
        webSocketManager.start(this);

        finish();
    }


    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}