package com.example.parstagram.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser implements Parcelable {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_NUM_POSTS = "totalPosts";
    public static final String KEY_LIKED_POSTS = "likedPosts";


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

    public ParseRelation<Post> getPostsRelation() {
        return getRelation(KEY_LIKED_POSTS);
    }

    public void addPost(Post post) {
        getPostsRelation().add(post);
        saveInBackground();
    }

    public void removePost(Post post) {
        getPostsRelation().remove(post);
        saveInBackground();
    }

    public boolean likesPost(Post post) throws ParseException {
        List<Post> liked = getPostsRelation().getQuery().find();
        for (Post post1 : liked) {
            if (post.getObjectId().equals(post1.getObjectId())) {
                return true;
            }
        }
        return false;
    }



}
