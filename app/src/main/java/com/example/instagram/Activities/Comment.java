package com.example.instagram.Activities;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT = "comment";

    public Comment() {
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }
}
