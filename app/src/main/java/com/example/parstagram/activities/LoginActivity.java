package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parstagram.databinding.ActivityLoginBinding;
import com.example.parstagram.models.User;
import com.example.parstagram.models.UserInfo;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

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

        if (ParseUser.getCurrentUser() != null) {
            User user = (User) ParseUser.getCurrentUser();
            UserInfo received = user.getUserInfo();
            if (received == null) {
                UserInfo newInfo = new UserInfo();
                newInfo.setTotalFollowers(0);
                newInfo.setTotalFollowing(0);
                user.setUserInfo(newInfo);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        gotoMainActivity();
                    }
                });
            } else {
                gotoMainActivity();
            }

        }
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
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        final User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setDescription("");
        newUser.setTotalPosts(0);
        UserInfo info = new UserInfo();
        newUser.setUserInfo(info);
        int drawableId = getResources().getIdentifier("default_profile", "drawable", getPackageName());
        Drawable d = getDrawable(drawableId);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        final ParseFile file = new ParseFile(bitmapdata);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                // If successful add file to user and signUpInBackground
                if(null == e) {
                    newUser.setProfilePicture(file);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Failed to sign up user.", e);
                                Toast.makeText(LoginActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "Successfully signed up user.");
                                gotoMainActivity();
                            }
                        }
                    });
                }
            }
        });

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
                    User tempUser = (User) user;
                    UserInfo received = tempUser.getUserInfo();
                    if (received == null) {
                        UserInfo newInfo = new UserInfo();
                        newInfo.setTotalFollowers(0);
                        newInfo.setTotalFollowing(0);
                        tempUser.setUserInfo(newInfo);
                        tempUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i(TAG, "Sign in successful.");
                                gotoMainActivity();
                            }
                        });
                    } else {
                        UserInfo newInfo = new UserInfo();
                        newInfo.setTotalFollowers(0);
                        newInfo.setTotalFollowing(0);
                        tempUser.setUserInfo(newInfo);
                        tempUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i(TAG, "Sign in successful.");
                                gotoMainActivity();
                            }
                        });
                    }

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