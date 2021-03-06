package com.example.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Activities.FActivity;
import com.example.instagram.Activities.MainActivity;
import com.example.instagram.Models.Post;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.Adapters.postGridAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    public static final String TAG = "ProfileFragment";

    protected postGridAdapter adapter;
    protected List<Post> allPosts;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        init(); // Needed for some reason or all the code gets called
    }

    private void init() {
        allPosts = new ArrayList<>();
        adapter = new postGridAdapter(getContext(), allPosts);
        binding.rvGridPosts.setAdapter(adapter);
        binding.rvGridPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        ParseFile image = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (image != null) {
            Glide.with(this).load(image.getUrl()).apply(RequestOptions.circleCropTransform()).into(binding.ivProfilePicture);
        }

        // Only signs out!
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                getActivity().finish();
            }
        });

        binding.tvProfileFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FActivity.class);
                intent.putExtra("code", "Followers");
                startActivity(intent);
            }
        });

        binding.tvProfileFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FActivity.class);
                intent.putExtra("code", "Following");
                startActivity(intent);
            }
        });

        // Querying the parse database for post
        queryPosts();
    }

    // Querying the database for post
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                binding.tvProfilePosts.setText(allPosts.size() + "\n Posts");
            }
        });
    }
}