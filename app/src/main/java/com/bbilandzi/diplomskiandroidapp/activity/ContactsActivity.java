package com.bbilandzi.diplomskiandroidapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.adapter.ViewPagerAdapter;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContactsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Users");
            } else {
                tab.setText("Groups");
            }
        }).attach();

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> logout());
    }


    private void logout() {
        AuthUtils.clearToken(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
