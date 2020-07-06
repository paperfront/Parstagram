package com.example.parstagram.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.models.Post;
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
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String PHOTO_FILENAME = "photo.jpg";

    private ActivityMainBinding binding;

    private View rootView;
    private EditText etDescription;
    private Button btTakePicture;
    private Button btSubmit;
    private ImageView ivPicture;

    private List<Post> posts;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);

        bindElements();
        setupButtons();

    }

    private void bindElements() {
        etDescription = binding.etDescription;
        btTakePicture = binding.btTakePicture;
        btSubmit = binding.btSubmit;
        ivPicture = binding.ivPicture;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPicture.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(PHOTO_FILENAME);
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this,
                "com.codepath.fileprovider.parstagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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


    private void handleSubmit() {
        String description = etDescription.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, "Description is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile == null || ivPicture.getDrawable() == null) {
            Toast.makeText(this, "There is no image. Please take a picture and try again."
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser user = ParseUser.getCurrentUser();
        savePost(description, user);

    }

    private void savePost(String description, ParseUser user) {
        Post post = new Post();
        post.setAuthor(user);
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to save post", e);
                    Toast.makeText(MainActivity.this, "Failed to save post", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Successfully saved post.");
                    Toast.makeText(MainActivity.this, "Successfully made post!", Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                }
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query", e);
                } else {
                    Log.i(TAG, "Query successful.");
                }
            }
        });
    }
}