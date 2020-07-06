package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parstagram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;

    private View rootView;
    private EditText etUsername;
    private EditText etPassword;
    private Button btLogin;
    private Button btSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);

        bindElements();
        setupButtons();
    }

    private void bindElements() {
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        btLogin = binding.btLogin;
        btSignup = binding.btSignup;
    }

    private void setupButtons() {
        setupLoginButton();
        setupSignupButton();
    }

    private void setupSignupButton() {
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });
    }

    private void handleSignup() {
    }

    private void setupLoginButton() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        Log.i(TAG, "Performing login");

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with sign-in.", e);
                    Toast.makeText(LoginActivity.this, "Login failed! Please make sure credentials " +
                            "are correct and try again.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Sign in successful.");
                    gotoMainActivity();
                }

            }
        });
    }

    private void gotoMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


}