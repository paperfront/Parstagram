package com.example.parstagram.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.File;

public class ImageUtils {

    public static final String TAG = "ImageUtils";

    public static void loadImages(final Context context, final ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Glide.with(context).load(bmp).into(img);
                    } else {
                        Log.e(TAG, "Failed to decode image.", e);
                        loadImages(context, thumbnail, img);
                    }
                }
            });
        } else {
            img.setImageResource(R.drawable.ic_launcher_background);
        }
    }// load image

    public static void loadProfile(final Context context, ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Glide.with(context).load(bmp).circleCrop().into(img);
                    } else {
                    }
                }
            });
        } else {
            img.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public static File getPhotoFileUri(Context context, String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static void loadDefaultProfilePic(Context context, ImageView iv) {
        int drawableId = context.getResources().getIdentifier("default_profile", "drawable", context.getPackageName());
        Drawable drawable = context.getDrawable(drawableId);
        Glide.with(context).load(drawable).circleCrop().into(iv);
    }

}
