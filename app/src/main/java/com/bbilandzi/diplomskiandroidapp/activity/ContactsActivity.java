package com.bbilandzi.diplomskiandroidapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.adapter.ViewPagerAdapter;
import com.bbilandzi.diplomskiandroidapp.fragments.CreateGroupDialogFragment;
import com.bbilandzi.diplomskiandroidapp.fragments.GroupListFragment;
import com.bbilandzi.diplomskiandroidapp.fragments.UserListFragment;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContactsActivity extends BaseActivity implements CreateGroupDialogFragment.CreateGroupDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.activity_contacts);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Users" : "Groups")
        ).attach();
    }

    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new UserListFragment());
        adapter.addFragment(new GroupListFragment());
        viewPager.setAdapter(adapter);
    }


    private void logout() {
        AuthUtils.clearToken(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onSubmit(String groupName, List<Long> selectedUser) {
        Log.d("Users", selectedUser.toString());
    }
}
