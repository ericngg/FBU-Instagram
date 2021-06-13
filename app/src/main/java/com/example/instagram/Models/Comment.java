package com.example.instagram.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_OBJECT_ID = "postObjectId";

    public Comment() {
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }

    public void setPostObjectId(String objectId) {
        put(KEY_OBJECT_ID, objectId);
    }

    public String getPostObjectId() {
        return getString(KEY_OBJECT_ID);
    }
}
