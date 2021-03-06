package com.example.parstagram.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_NUM_COMMENTS = "totalComments";
    public static final String KEY_NUM_LIKES = "totalLikes";



    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(User user) {
        put(KEY_AUTHOR, user);
    }

    public void setTotalComments(int num) {
        put(KEY_NUM_COMMENTS, num);
    }

    public int getTotalComments() {return getInt(KEY_NUM_COMMENTS); }

    public void incrementComments() {
        put(KEY_NUM_COMMENTS, 1 + getInt(KEY_NUM_COMMENTS));
        saveInBackground();
    }

    public void setTotalLikes(int num) {
        put(KEY_NUM_LIKES, num);
    }

    public int getTotalLikes() {return getInt(KEY_NUM_LIKES); }

    public void incrementLikes() {
        put(KEY_NUM_LIKES, 1 + getInt(KEY_NUM_LIKES));
        saveInBackground();
    }

    public void decrementLikes() {
        put(KEY_NUM_LIKES, getInt(KEY_NUM_LIKES) - 1);
        saveInBackground();
    }



}
