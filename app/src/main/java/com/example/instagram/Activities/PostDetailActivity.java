package com.example.instagram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Models.Comment;
import com.example.instagram.Models.Post;
import com.example.instagram.Adapters.commentAdapter;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    public static final int COMMENT_TAG = 92;

    ActivityPostDetailBinding binding;

    List<Comment> allComments;
    String objectId;
    commentAdapter adapter;
    Handler handler;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);

        // Initialize variables
        allComments = new ArrayList<>();
        handler = new Handler();

        // Intent
        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");
        objectId = post.getObjectId();

        // Profile picture
        ParseFile image = post.getProfilePicture();
        Glide.with(this).load(image.getUrl()).apply(RequestOptions.circleCropTransform()).into(binding.ivDetailProfile);


        // Post picture
        Glide.with(this).load(post.getImage().getUrl()).into(binding.ivDetailPicture);

        // Username, description, number of likes, and createdAt
        binding.tvDetailName.setText(post.getUser().getUsername());
        binding.tvDetailDescription.setText(post.getDescription());
        binding.tvDetailLikes.setText(post.getLikes() + " Likes");
        binding.tvCreatedAt.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

        // adapter
        adapter = new commentAdapter(this, allComments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        // Queries the parse database for comments
        queryComments();

        // This triggers when the user presses the comments button, rather than clicking on the post (focuses on the edit text)
        if (intent.getIntExtra("code", 80) == COMMENT_TAG) {
            binding.etComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etComment, InputMethodManager.SHOW_IMPLICIT);
        }

        // Sends the comment created to the parse database
        binding.btnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                String comment = binding.etComment.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(PostDetailActivity.this, "Comment cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                saveComment(currentUser, comment);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        queryComments();
                        binding.rvComments.smoothScrollToPosition(0);
                        hideProgressBar();
                    }
                }, 3000);
            }
        });
    }

    // For the loading spin
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // For the loading spin
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    // Shows the loading spin
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    // Hides the loading spin
    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    // Saves the comment to the parse database
    private void saveComment(ParseUser currentUser, String comment) {
        ParseObject comm = ParseObject.create("Comments");
        comm.put("postObjectId", objectId);
        comm.put("user", currentUser);
        comm.put("comment", comment);

        comm.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed: ");
                } else {
                    Log.i(TAG, "Comment save was successful");
                    binding.etComment.setText("");
                }
            }
        });


        /*
        Comment com = new Comment();
        com.setComment(comment);
        com.setUser(currentUser);
        com.setObjectId(objectId);

        com.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                } else {
                    Log.i(TAG, "Comment save was successful");
                    binding.etComment.setText("");
                }
            }
        });
         */
    }

    // Converts the createdAt time to a more readable time
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

    // Queries the parse database for all comments associated with a post
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