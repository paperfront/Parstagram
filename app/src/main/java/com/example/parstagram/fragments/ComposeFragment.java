package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentComposeBinding;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    private FragmentComposeBinding binding;
    public static final String TAG = "ComposeFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String PHOTO_FILENAME = "photo.jpg";

    private View rootView;
    private EditText etDescription;
    private Button btTakePicture;
    private Button btSubmit;
    private ImageView ivPicture;
    private ProgressBar pbLoading;

    private File photoFile;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
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
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentComposeBinding.bind(view);
        bindElements();
        setupButtons();
        launchCamera();
    }

    private void bindElements() {
        etDescription = binding.etDescription;
        btTakePicture = binding.btTakePicture;
        btSubmit = binding.btSubmit;
        ivPicture = binding.ivPicture;
        pbLoading = binding.pbLoading;
    }

    private void setupButtons() {
        setupTakePictureButton();
        setupSubmitButton();
    }

    private void setupTakePictureButton() {
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Take Picture Button clicked.");
                launchCamera();
            }
        });
    }

    private void setupSubmitButton() {
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Submit button clicked.");
                handleSubmit();
            }
        });

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
                ivPicture.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

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






    private void handleSubmit() {
        pbLoading.setVisibility(View.VISIBLE);
        String description = etDescription.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile == null || ivPicture.getDrawable() == null) {
            Toast.makeText(getContext(), "There is no image. Please take a picture and try again."
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        User user = (User) ParseUser.getCurrentUser();
        savePost(description, user);

    }

    private void savePost(String description, User user) {
        Post post = new Post();
        user.incrementPosts();
        post.setAuthor(user);
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setTotalComments(0);
        post.setTotalLikes(0);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                pbLoading.setVisibility(View.INVISIBLE);
                if (e != null) {
                    Log.e(TAG, "Failed to save post", e);
                    Toast.makeText(getContext(), "Failed to save post", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Successfully saved post.");
                    Toast.makeText(getContext(), "Successfully made post!", Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                    ivPicture.setImageDrawable(null);
                }
            }
        });
    }


}