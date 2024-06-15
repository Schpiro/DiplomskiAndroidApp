package com.bbilandzi.diplomskiandroidapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContactsActivity extends AppCompatActivity {
    ContactViewModel contactViewModel;
    private LinearLayout usersContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Initialize UI components
        usersContainer = findViewById(R.id.usersContainer);

        // Observe changes in fetched users
        // Update UI with fetched users
        contactViewModel.getFetchedUsers().observe(this, this::displayUsers);

        // Trigger fetching of users
        contactViewModel.getAllUsers();

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> logout());

        Button getAllUsersButton = findViewById(R.id.button);
        Button getAllUserGroupsButton = findViewById(R.id.button2);
        Button getUsersInGroupButton = findViewById(R.id.button3);
        getAllUsersButton.setOnClickListener(v -> getAllUsers());
        getAllUserGroupsButton.setOnClickListener(v -> logout());
        getUsersInGroupButton.setOnClickListener(v -> logout());

    }

    private void getAllUsers() {
        contactViewModel.getAllUsers();
    }

    private void logout() {
        AuthUtils.clearToken(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayUsers(List<UserDTO> users) {
        usersContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        // Add TextViews for each user
        for (UserDTO user : users) {
            TextView textView = (TextView) inflater.inflate(R.layout.user_item, usersContainer, false);
            textView.setText(user.getUsername());
            textView.setOnClickListener(v -> {
                // Handle click event to redirect to another activity
                Intent intent = new Intent(ContactsActivity.this, MessengerActivity.class);
                intent.putExtra("userId", user.getId()); // Pass user info using Intent extras
                startActivity(intent);
            });

            // Add the TextView to the LinearLayout
            usersContainer.addView(textView);
        }
    }
}
