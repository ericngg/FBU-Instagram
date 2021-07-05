package com.example.instagram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instagram.Adapters.fAdapter;
import com.example.instagram.Models.Follow;
import com.example.instagram.Models.Post;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityFBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FActivity extends AppCompatActivity {

    ActivityFBinding binding;
    public static final String TAG = "FActivity";

    Intent intent;
    String code;
    ArrayList<ParseUser> users;
    fAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        users = new ArrayList<>();
        intent = getIntent();
        code = intent.getStringExtra("code");

        getSupportActionBar().setTitle(code);

        adapter = new fAdapter(users, this);
        binding.rvF.setAdapter(adapter);
        binding.rvF.setLayoutManager(new LinearLayoutManager(this));

        queryPosts(code);
    }

    // Querying the database for post
    private void queryPosts(String code) {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.include(Follow.KEY_FOLLOWER);
        query.include(Follow.KEY_FOLLOWING);
        if (code.equals("Followers")) {
            query.whereEqualTo("following", ParseUser.getCurrentUser());
        } else {
            query.whereEqualTo("follower", ParseUser.getCurrentUser());
        }

        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> follow, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (code.equals("Followers")) {
                    for (Follow person : follow) {
                        users.add(person.getFollowerUser());
                    }
                } else {
                    for (Follow person : follow) {
                        users.add(person.getFollowingUser());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}