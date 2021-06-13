package com.example.instagram.Adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Models.Comment;
import com.example.instagram.R;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder> {

    public static Context context;
    List<Comment> comments;

    public commentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new commentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCommentPicture;
        private TextView tvComment;
        private TextView tvCommentCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCommentPicture = itemView.findViewById(R.id.ivCommentPicture);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCommentCreatedAt = itemView.findViewById(R.id.tvCommentCreatedAt);
        }

        public void bind(Comment comment) {
            // Note: add this if multiple users. I hardcoded the source picture because it is round
            // and if you use this code, it grabs a square one from the database. Atm, it is designed
            // for just me (Eric).

            /*
            ParseFile image = comment.getUser().getParseFile("profilePicture");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivCommentPicture);
            }

             */

            // Populating comment data
            String text = "<b>" + comment.getUser().getUsername() + "</b> " + comment.getComment();
            tvComment.setText(Html.fromHtml(text));
            tvCommentCreatedAt.setText(getRelativeTimeAgo(comment.getCreatedAt().toString()));

        }

        // Converts the createdAt time to a more readable and useable timestamp.
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
