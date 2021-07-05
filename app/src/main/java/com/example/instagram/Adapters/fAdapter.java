package com.example.instagram.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.R;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

public class fAdapter extends RecyclerView.Adapter<fAdapter.ViewHolder> {

    ArrayList<ParseUser> users;
    Context context;

    public fAdapter(ArrayList<ParseUser> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false);
        return new fAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFollowName;
        ImageView ivFollowProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFollowName = itemView.findViewById(R.id.tvFollowName);
            ivFollowProfilePic = itemView.findViewById(R.id.ivFollowProfilePic);
        }

        public void bind(ParseUser user) {
            tvFollowName.setText(user.getUsername());

            ParseFile image = user.getParseFile("profilePicture");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivFollowProfilePic);
            }
        }
    }
}
