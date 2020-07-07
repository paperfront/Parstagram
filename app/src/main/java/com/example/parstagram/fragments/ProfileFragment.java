package com.example.parstagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.activities.LoginActivity;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.databinding.FragmentHomeBinding;
import com.example.parstagram.databinding.FragmentProfileBinding;
import com.example.parstagram.databinding.TextviewCounterBinding;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public static final String TAG = "ProfileFragment";

    private TextView tvDescription;
    private TextView tvUsername;
    TextviewCounterBinding postsBinding;
    TextviewCounterBinding followersBinding;
    TextviewCounterBinding followingBinding;
    private Button btLogout;
    private ImageView ivProfilePicture;

    private ParseUser currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);
        bindElements();
        setupElements();
    }

    private void bindElements() {
        tvDescription = binding.tvDescription;
        tvUsername = binding.tvUsername;
        btLogout = binding.btLogout;
        ivProfilePicture = binding.ivProfilePicture;
        postsBinding = binding.tvCounterPosts;
        followersBinding = binding.tvCounterFollowers;
        followingBinding = binding.tvCounterFollowing;
        currentUser = ParseUser.getCurrentUser();
    }

    private void setupElements() {
        setupCounters();
        setupText();
        setupImage();
        setupButtons();
    }

    private void setupCounters() {
        postsBinding.tvWord.setText(R.string.posts);
        postsBinding.tvCounter.setText("300");
        followingBinding.tvWord.setText(R.string.following);
        followingBinding.tvCounter.setText("300");
        followersBinding.tvWord.setText(R.string.followers);
        followersBinding.tvCounter.setText("300");
    }

    private void setupText() {
        tvUsername.setText(currentUser.getUsername());
        tvDescription.setText("Sample Description");
    }

    private void setupImage() {
        if (currentUser.has(User.KEY_PROFILE_PICTURE)) {
            ParseFile profileFile = (ParseFile) currentUser.get(User.KEY_PROFILE_PICTURE);
            ImageUtils.loadImages(profileFile, ivProfilePicture);
        } else {
            ImageUtils.loadDefaultProfilePic(getContext(), ivProfilePicture);
        }

    }

    private void setupButtons() {
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Log out button clicked.");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }
}