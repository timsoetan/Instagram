package com.example.instagram.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.instagram.Fragments.EmptyFragment;
import com.example.instagram.Fragments.PostFragment;
import com.example.instagram.Fragments.TimelineFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationItemView user;
    private BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.ig_status_bar));

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.centerView, new TimelineFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        // Find references for the views
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        user = findViewById(R.id.navUser);

        // Set up the navigation bar to switch between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment;
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        switch (menuItem.getItemId()) {
                            case R.id.navHome:
                                fragment = new TimelineFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                            case R.id.navSearch:
                                fragment = new EmptyFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                            case R.id.navPost:
                                fragment = new PostFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                            case R.id.navHeart:
                                fragment = new EmptyFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                            case R.id.navUser:
                                fragment = new PostFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                            default:
                                fragment = new TimelineFragment();
                                fragmentTransaction.addToBackStack("post");
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.centerView, fragment).commit();
                        return true;
                    }
                });

        // Set up the user profile button to log out on long click
        user.setLongClickable(true);
        user.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        HomeActivity.this);
                alert.setTitle("Log Out");
                alert.setIcon(R.drawable.ic_alert);
                alert.setMessage("Are you sure you want to log out?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("HomeActivity", "Post[" + i + "]"
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


    }
}