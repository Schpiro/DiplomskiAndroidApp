package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bbilandzi.diplomskiandroidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewGroup container;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        container = findViewById(R.id.fragment_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                if (!(this instanceof ProfileActivity)) {
                    startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
                return true;
            }
            if (item.getItemId() == R.id.nav_contacts) {
                if (!(this instanceof ContactsActivity)) {
                    startActivity(new Intent(BaseActivity.this, ContactsActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
                return true;
            }
            if (item.getItemId() == R.id.nav_events) {
                if (!(this instanceof EventListActivity)) {
                    startActivity(new Intent(BaseActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if(bottomNavigationView != null) {
            switch (getClass().getSimpleName()) {
                case "ProfileActivity":
                    bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                    break;
                case "ContactsActivity":
                    bottomNavigationView.setSelectedItemId(R.id.nav_contacts);
                    break;
                case "EventListActivity":
                    bottomNavigationView.setSelectedItemId(R.id.nav_events);
                    break;
            }
        }
    }

    protected void setActivityContent(@LayoutRes int layoutResID) {
        container.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, container, true);
    }
}
