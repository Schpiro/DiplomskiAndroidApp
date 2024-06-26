package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.AuthRequest;
import com.bbilandzi.diplomskiandroidapp.utils.AuthCallback;
import com.bbilandzi.diplomskiandroidapp.viewmodel.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint

public class AuthActivity extends AppCompatActivity implements AuthCallback {
    AuthViewModel authViewModel;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (isLogin) {
            setContentView(R.layout.activity_login);
            setupLoginViews();
        } else {
            setContentView(R.layout.activity_register);
            setupRegisterViews();
        }
    }

    private void setupLoginViews() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.submit_button);
        TextView authSwitch = findViewById(R.id.auth_switch_text);

        loginButton.setOnClickListener(v -> {
            try {
                login();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        authSwitch.setOnClickListener(v -> {
            try {
                switchLayout();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupRegisterViews() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.submit_button);
        TextView authSwitch = findViewById(R.id.auth_switch_text);

        registerButton.setOnClickListener(v -> {
            try {
                register();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        authSwitch.setOnClickListener(v -> {
            try {
                switchLayout();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void switchLayout() {
        isLogin = !isLogin;

        if (isLogin) {
            setContentView(R.layout.activity_login);
            setupLoginViews();
        } else {
            setContentView(R.layout.activity_register);
            setupRegisterViews();
        }
    }

    private void login() {
        authViewModel.login(this, getLoginInfo(), this);
    }

    private void register() {
        authViewModel.register(this, getLoginInfo(), this);
    }

    private AuthRequest getLoginInfo() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean testing = false ;
        if(testing) {
            username = "admin";
            password = "admin";
        } else {
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        }

        return new AuthRequest(username, password);
    }

    private void goToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthSuccess(String token) {
        goToContacts();
    }

    @Override
    public void onAuthError(String errorMessage) {
        Log.d("AuthActivity", "Failed to authenticate, try again!");
    }
}
