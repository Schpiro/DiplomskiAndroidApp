package com.bbilandzi.diplomskiandroidapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bbilandzi.diplomskiandroidapp.model.LoginRequest;
import com.bbilandzi.diplomskiandroidapp.model.LoginResponse;
import com.bbilandzi.diplomskiandroidapp.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        /*
        //for reading username + password
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }*/

        ApiService apiService = Network.getClient(getApplicationContext()).create(ApiService.class);
        if (apiService == null) {
            Toast.makeText(this, "Failed to create API service", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create login request
        LoginRequest loginRequest = new LoginRequest("admin", "admin");

        // Make login API call
        Call<LoginResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getJwt();
                    Log.d("Login Success", "JWT Token: " + token);
                    // Save token or proceed with authenticated actions
                } else {
                    Log.e("Login Error", "Failed to authenticate user");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Login Error", "Failed to connect to authentication server: " + t.getMessage());
            }
        });
    }
}