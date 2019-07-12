package com.example.instagram.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Activities.PostDetailActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import static android.view.View.GONE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> posts;
    Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Post post = posts.get(position);
                Long numLikes = (long) post.getNumLikes();
                if (post.isLiked()) {
                    post.unlikePost(ParseUser.getCurrentUser());
                    toggleLike(holder.ivLike, post.isLiked());
                    updateLikes(holder, position, numLikes - 1);
                } else {
                    post.likePost(ParseUser.getCurrentUser());
                    toggleLike(holder.ivLike, post.isLiked());
                    updateLikes(holder, position, numLikes + 1);
                }
            }
        });

        return holder;
    }

    private void toggleLike(ImageView image, boolean liked) {
        if (liked) {
            image.setBackgroundResource(R.drawable.ic_heart_red);
        } else {
            image.setBackgroundResource(R.drawable.ic_heart_stroke);
        }
    }

    public void updateLikes(ViewHolder holder, int position, Long numLikes) {
        Post post = posts.get(position);

        if (numLikes == 0L) {
            holder.tvLikesCount.setVisibility(GONE);
            holder.tvLikes.setVisibility(GONE);
        }

        if (numLikes == 1L) {
            holder.tvLikesCount.setVisibility(View.VISIBLE);
            holder.tvLikes.setVisibility(View.VISIBLE);
            holder.tvLikes.setText("like");
            holder.tvLikesCount.setText(String.valueOf(numLikes));
        }

        if (numLikes > 1L) {
            holder.tvLikesCount.setVisibility(View.VISIBLE);
            holder.tvLikes.setVisibility(View.VISIBLE);
            holder.tvLikes.setText("likes");
            holder.tvLikesCount.setText(String.valueOf(numLikes));
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Post post = posts.get(position);

        updateLikes(holder, position, (long) post.getNumLikes());
        toggleLike(holder.ivLike, post.isLiked());

        holder.tvUsername.setText(post.getUser().getUsername());

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(holder.ivPostImage);
        }

        ParseFile profileImage = post.getUser().getParseFile(Post.getKeyProfileImage());
        if (profileImage != null) {
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.default_profile_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }

        if (post.getDescription().trim().length() == 0) {
            holder.tvCaption.setVisibility(GONE);
        } else {
            holder.tvCaption.setVisibility(View.VISIBLE);

            String username = post.getUser().getUsername();
            String caption = post.getDescription();
            SpannableString str = new SpannableString(username + " " + caption);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvCaption.setText(str);
        }

        holder.tvTimeAgo.setText(post.getRelativeTimeAgo());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileImage;
        private ImageView ivPostImage;
        private TextView tvUsername;
        private TextView tvCaption;
        private TextView tvLikesCount;
        private TextView tvLikes;
        private TextView tvTimeAgo;
        private ImageView ivLike;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivLike = itemView.findViewById(R.id.ivLike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {

                        Post post = posts.get(position);

                        Intent intent = new Intent(context, PostDetailActivity.class);
                        intent.putExtra(Post.class.getSimpleName(), post.getObjectId());
                        intent.putExtra("post's user", post.getUser());
                        context.startActivity(intent);
                    }
                }
            });
        }
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
}


