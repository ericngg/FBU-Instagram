package com.example.instagram.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Follow")
public class Follow extends ParseObject {
    public static final String KEY_FOLLOWER = "follower";
    public static final String KEY_FOLLOWING = "following";

    public Follow() {
    }

    public ParseUser getFollowerUser() {
        return getParseUser(KEY_FOLLOWER);
    }

    public ParseUser getFollowingUser() {
        return getParseUser(KEY_FOLLOWING);
    }
}
