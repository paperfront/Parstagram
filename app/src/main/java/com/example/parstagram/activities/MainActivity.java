package com.example.parstagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.HomeFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment composeFragment = ComposeFragment.newInstance();
    final Fragment homeFragment = HomeFragment.newInstance();
    final Fragment profileFragment = ProfileFragment.newInstance();


    private ActivityMainBinding binding;

    private View rootView;
    private BottomNavigationView bottomNavigation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        bindElements();
        setupElements();

    }

    private void bindElements() {
        bottomNavigation = binding.bottomNavigation;
    }

    private void setupElements() {
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = homeFragment;
                        break;
                    case R.id.action_post:
                        fragment = composeFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ProfileFragment.KEY_PROFILE, (User) ParseUser.getCurrentUser());
                        fragment.setArguments(bundle);
                        break;
                    default:
                        Log.e(TAG, "Navigation item clicked does not have a case. Setting clicked item to home...");
                        fragment = homeFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.action_home);
    }



}