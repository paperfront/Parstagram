package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.parstagram.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

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
    }

    private void bindElements() {
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        btLogin = binding.btLogin;
        btSignup = binding.btSignup;
    }
}