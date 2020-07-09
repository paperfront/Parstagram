package com.example.parstagram.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_NUM_FOLLOWERS = "totalFollowers";
    public static final String KEY_NUM_FOLLOWING = "totalFollowing";


    public ParseRelation<User> getFollowersRelation() {
        return getRelation(KEY_FOLLOWERS);
    }

    public void addFollower(User user) {
        getFollowersRelation().add(user);
        incrementFollowers();
        saveInBackground();
    }

    public void removeFollower(User user) {
        getFollowersRelation().remove(user);
        decrementFollowers();
        saveInBackground();
    }

    public int getTotalFollowing() {
        return getInt(KEY_NUM_FOLLOWING);
    }

    public int getTotalFollowers() {
        return getInt(KEY_NUM_FOLLOWERS);
    }

    public void incrementFollowing() {
        put(KEY_NUM_FOLLOWING, 1 + getInt(KEY_NUM_FOLLOWING));
        saveInBackground();
    }

    public void decrementFollowing() {
        put(KEY_NUM_FOLLOWING, getInt(KEY_NUM_FOLLOWING) - 1);
        saveInBackground();
    }

    public void incrementFollowers() {
        put(KEY_NUM_FOLLOWERS, 1 + getInt(KEY_NUM_FOLLOWERS));
        saveInBackground();
    }

    public void decrementFollowers() {
        put(KEY_NUM_FOLLOWERS, getInt(KEY_NUM_FOLLOWERS) - 1);
        saveInBackground();
    }

    public ParseRelation<User> getFollowingRelation() {
        return getRelation(KEY_FOLLOWING);
    }

    public void addFollowing(User user) {
        getFollowingRelation().add(user);
        incrementFollowing();
        saveInBackground();
    }

    public void removeFollowing(User user) {
        getFollowingRelation().remove(user);
        decrementFollowing();
        saveInBackground();
    }

    public void setTotalFollowers(int num) {
        put(KEY_NUM_FOLLOWERS, num);
    }

    public void setTotalFollowing(int num) {
        put(KEY_NUM_FOLLOWING, num);
    }




}