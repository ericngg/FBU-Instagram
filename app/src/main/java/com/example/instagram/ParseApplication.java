package com.example.instagram;

import android.app.Application;

import com.example.instagram.Models.Comment;
import com.example.instagram.Models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("HVZbUnXAQJetaugySLCBnpKKXsRsfxpQDdJW9KQy")
                .clientKey("erROWmjzqI7g5qYRlDl4ETqWdLVH1xouK88Qec2M")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}
