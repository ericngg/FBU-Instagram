package com.example.instagram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagram.Post;
import com.example.instagram.R;
import com.example.instagram.commentAdapter;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";

    ActivityPostDetailBinding binding;

    List<Comment> allComments;
    String objectId;

    commentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);

        allComments = new ArrayList<>();

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");
        objectId = post.getObjectId();

        Glide.with(this).load(post.getImage().getUrl()).into(binding.ivDetailPicture);
        binding.tvDetailName.setText(post.getUser().getUsername());
        binding.tvDetailDescription.setText(post.getDescription());

        binding.tvCreatedAt.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

        adapter = new commentAdapter(this, allComments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        queryComments();
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereEqualTo("postObjectId", objectId);
        query.findInBackground(new FindCallback<Comment>() {

            @Override
            public void done(List<Comment> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }

                allComments.addAll((objects));
                adapter.notifyDataSetChanged();
            }
        });
    }
}