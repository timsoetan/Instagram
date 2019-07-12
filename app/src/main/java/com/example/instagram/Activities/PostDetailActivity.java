package com.example.instagram.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import static android.view.View.GONE;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private ImageView ivPostImage;
    private TextView tvUsername;
    private TextView tvCaption;
    private TextView tvLikesCount;
    private TextView tvLikes;
    private TextView tvTimeAgo;
    private ImageView ivLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.ig_status_bar2));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivPostImage =  findViewById(R.id.ivPostImage);
        tvUsername = findViewById(R.id.tvUsername);
        tvTimeAgo = findViewById(R.id.tvTimeAgo);
        tvCaption = findViewById(R.id.tvCaption);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvLikes = findViewById(R.id.tvLikes);
        ivLike = findViewById(R.id.ivLike);

        String postId = getIntent().getStringExtra(Post.class.getSimpleName());
        final ParseUser user = getIntent().getParcelableExtra("post's user");

        Post.Query query = new Post.Query();

        query.withUser().getInBackground(postId, new GetCallback<Post>() {
            @Override
            public void done(final Post post, ParseException e) {
                if (e == null) {
                    // Set the handle text
                    tvUsername.setText(post.getUser().getUsername());

                    // Get the image and load it (if possible)
                    ParseFile image = post.getImage();
                    if (image != null) {
                        Glide.with(getApplicationContext())
                                .load(image.getUrl())
                                .into(ivPostImage);
                    }

                    // Get the profile image and load it
                    ParseUser user = post.getUser();
                    ParseFile profileImage = user.getParseFile(Post.getKeyProfileImage());
                    if (profileImage != null) {
                        Glide.with(getApplicationContext())
                                .load(profileImage.getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfileImage);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.default_profile_image)
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfileImage);
                    }

                    if (post.getDescription().trim().length() == 0) {
                        tvCaption.setVisibility(GONE);
                    } else {
                        tvCaption.setVisibility(View.VISIBLE);

                        String username = post.getUser().getUsername();
                        String caption = post.getDescription();
                        SpannableString str = new SpannableString(username + " " + caption);
                        str.setSpan(new StyleSpan(Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvCaption.setText(str);
                    }

                    // Set relative timestamp
                    tvTimeAgo.setText(post.getRelativeTimeAgo());

                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

