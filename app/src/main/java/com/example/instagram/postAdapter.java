package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activities.MainActivity;
import com.example.instagram.Activities.PostDetailActivity;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {
    private static Context context;
    private List<Post> posts;
    private MainActivity mainActivity;

    public static final String TAG = "postAdapter";


    public postAdapter(Context context, List<Post> posts, MainActivity mainActivity) {
        this.context = context;
        this.posts = posts;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                Post post = posts.get(vh.getAdapterPosition());
                intent.putExtra("post", (Parcelable) post);
                context.startActivity(intent);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, mainActivity);
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvCreatedAt;
        private TextView tvLikes;
        private ImageView ivPostProfile;

        private ImageButton ibLike;
        private ImageButton ibComment;

        private int likesCount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivPostProfile = itemView.findViewById(R.id.ivPostProfile);

            ibLike = itemView.findViewById(R.id.ibLike);
            ibComment = itemView.findViewById(R.id.ibComment);
        }

        public void bind(Post post, MainActivity mainActivity) {
            tvDescription.setText(post.getDescription());
            tvName.setText(post.getUser().getUsername());
            tvCreatedAt.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));
            likesCount = post.getLikes();
            tvLikes.setText(likesCount + " Likes");
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            if (post.getLiked()) {
                ibLike.setImageResource(R.mipmap.ufiheart_active_foreground);
                ibLike.setTag(R.mipmap.ufiheart_active_foreground);
            } else {
                ibLike.setImageResource(R.mipmap.ufiheart_foreground);
                ibLike.setTag(R.mipmap.ufiheart_foreground);
            }

            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.getInBackground(post.getObjectId(), (object, e) -> {
                        if (e == null) {
                            if ((int) ibLike.getTag() == R.mipmap.ufiheart_foreground) {
                                // Like
                                likesCount++;
                                object.put("likesCount", likesCount);
                                object.put("liked", true);
                                ibLike.setImageResource(R.mipmap.ufiheart_active_foreground);
                                ibLike.setTag(R.mipmap.ufiheart_active_foreground);
                            } else {
                                // Unlike
                                likesCount--;
                                object.put("likesCount", likesCount);
                                object.put("liked", false);
                                ibLike.setImageResource(R.mipmap.ufiheart_foreground);
                                ibLike.setTag(R.mipmap.ufiheart_foreground);
                            }

                            // All other fields will remain the same
                            object.saveInBackground();
                            tvLikes.setText(likesCount + " Likes");

                        } else {
                            // something went wrong
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            ibComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open comments Activity with a recyclerview of the comments
                }
            });

            ivPostProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.goProfile();
                }
            });

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.goProfile();
                }
            });
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
    }
}
