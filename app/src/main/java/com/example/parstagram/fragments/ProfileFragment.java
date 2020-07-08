package com.example.parstagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.activities.LoginActivity;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.databinding.FragmentHomeBinding;
import com.example.parstagram.databinding.FragmentProfileBinding;
import com.example.parstagram.databinding.TextviewCounterBinding;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.models.ParseDataSourceFactory;
import com.example.parstagram.models.ParseGridDataSourceFactory;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public static final String TAG = "ProfileFragment";
    public static final String KEY_PROFILE = "profile";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String PHOTO_FILENAME = "profile.jpg";

    private TextView tvDescription;
    private TextView tvUsername;
    TextviewCounterBinding postsBinding;
    TextviewCounterBinding followersBinding;
    TextviewCounterBinding followingBinding;
    private Button btLogout;
    private ImageView ivProfilePicture;
    private RecyclerView rvGrid;
    private PostAdapter adapter;
    private RelativeLayout userButtonsHolder;

    private User currentUser;
    private File photoFile;
    private ParseGridDataSourceFactory factory;
    private LiveData<PagedList<Post>> posts;

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
        currentUser = getArguments().getParcelable(KEY_PROFILE);
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
        rvGrid = binding.rvGrid;
        userButtonsHolder = binding.userButtonsHolder;
    }

    private void setupElements() {
        setupCounters();
        setupText();
        setupImage();
        setupButtons();
        setupRV();
    }

    private void setupRV() {
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 3);
        adapter = new PostAdapter(getContext(), getActivity(), PostAdapter.TYPE_GRID);
        rvGrid.setAdapter(adapter);
        rvGrid.setLayoutManager(manager);
        setupData();

    }

    private void setupData() {
        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder().setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10).build();
        factory = new ParseGridDataSourceFactory(currentUser);

        posts = new LivePagedListBuilder(factory, pagedListConfig).build();
        posts.observe(getActivity(), new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable PagedList<Post> postList) {
                adapter.submitList(postList);
            }
        });
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
            ImageUtils.loadProfile(getContext(), profileFile, ivProfilePicture);
        } else {
            ImageUtils.loadDefaultProfilePic(getContext(), ivProfilePicture);
        }
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Profile Picture Clicked.");
                launchCamera();
            }
        });

    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = ImageUtils.getPhotoFileUri(getContext(), PHOTO_FILENAME);
        Uri fileProvider = FileProvider.getUriForFile(getContext(),
                "com.codepath.fileprovider.parstagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void setupButtons() {

        if (currentUser.hasSameId(ParseUser.getCurrentUser())) {
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
        } else {
            userButtonsHolder.setVisibility(View.GONE);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                Glide.with(getContext()).load(takenImage).circleCrop().into(ivProfilePicture);
                currentUser.setProfilePicture(new ParseFile(photoFile));
                try {
                    currentUser.save();
                } catch (ParseException e) {
                    Log.e(TAG, "Failed to save updated user.", e);
                }
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}