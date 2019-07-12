package com.example.instagram.Model;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Post")
public class Post  extends ParseObject {

    // Post attributes
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_PROFILE_IMAGE = "profileImage";
    private static final String KEY_LIKES = "likes";
    private static final String KEY_COMMENTS = "comments";

    public ParseObject getComments() {
        return getParseObject(KEY_COMMENTS);
    }

    public void addComment(ParseObject comments) {
        put(KEY_COMMENTS, comments);
    }

    public JSONArray getLikes() {
        return getJSONArray(KEY_LIKES);
    }

    public int getNumLikes() { return getLikes().length(); }

    public void likePost(ParseUser user) {
        add(KEY_LIKES, user);

        saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("POST", "Post liked!");
                    } else {
                        Log.e("ERROR", "Error liking post!");
                        e.printStackTrace();
                    }
                }
            });
    }

    public void unlikePost(ParseUser user) {
        ArrayList<ParseUser> a = new ArrayList<ParseUser>();
        a.add(user);
        removeAll(KEY_LIKES, a);

        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("POST", "Post unliked!");
                } else {
                    Log.e("ERROR", "Error unliking post!");
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isLiked() {
        JSONArray a = getLikes();
        if(a != null) {
            for (int i = 0; i < a.length(); i++) {
                try {
                    if (a.getJSONObject(i).getString("objectId").equals(ParseUser.getCurrentUser().getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void setLikes(ArrayList<ParseUser> users) {
        put(KEY_LIKES, users);
    }

    public static String getKeyDescription() {
        return KEY_DESCRIPTION;
    }

    public static String getKeyImage() {
        return KEY_IMAGE;
    }

    public static String getKeyUser() {
        return KEY_USER;
    }

    public static String getKeyCreatedAt() {
        return KEY_CREATED_AT;
    }

    public static String getKeyProfileImage() {
        return KEY_PROFILE_IMAGE;
    }

    public static String getKeyLikes() {
        return KEY_LIKES;
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(sf.format(this.getCreatedAt())).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate.toUpperCase();
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        // Get the first 20 posts
        public Query getTop() {
            setLimit(20);

            // Chronological feed
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Include user in the Query
        public Query withUser() {
            include("user");
            return this;
        }

        // Get post that is older than the maxDate.
        public Query getNext(Date maxDate) {
            whereLessThan(KEY_CREATED_AT, maxDate);
            return this;
        }
    }
}
