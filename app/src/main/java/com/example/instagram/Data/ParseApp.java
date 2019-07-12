package com.example.instagram.Data;

import android.app.Application;

import com.example.instagram.Model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-ig")
                .clientKey("penn-engineering")
                .server("http://instagram-android-client.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}