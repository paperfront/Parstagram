package com.example.parstagram.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser implements Parcelable {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_NUM_POSTS = "totalPosts";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile image) {
        put(KEY_PROFILE_PICTURE, image);
    }

    public void setTotalPosts(int num) {
        put(KEY_NUM_POSTS, num);
    }

    public int getTotalPosts() {return getInt(KEY_NUM_POSTS); }

    public void incrementPosts() {
        put(KEY_NUM_POSTS, 1 + getInt(KEY_NUM_POSTS));
        saveInBackground();
    }

}
