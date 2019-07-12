package com.example.instagram.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.instagram.Data.EndlessRecyclerViewScrollListener;
import com.example.instagram.Data.PostAdapter;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";
    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<Post> mPosts;
    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeContainer;

    protected EndlessRecyclerViewScrollListener scrollListener;

    protected ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);


        mPosts = new ArrayList<>();

        adapter = new PostAdapter(mPosts, getContext());

        rvPosts.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        setupSwipeRefreshing(view);

        enableEndlessScrolling();

        rvPosts.addOnScrollListener(scrollListener);

        loadTopPosts(new Date(0));
    }


    protected void enableEndlessScrolling() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTopPosts(getMaxDate());
            }
        };
    }


    protected void setupSwipeRefreshing(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchHomeAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_purple,
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
    }

    protected void fetchHomeAsync(int page) {
        adapter.clear();
        loadTopPosts(new Date(0));
        swipeContainer.setRefreshing(false);
    }


    protected void loadTopPosts(final Date maxDate) {

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            postsQuery.getTop().withUser();
        } else {
            postsQuery.getNext(maxDate).getTop().withUser();
        }

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {

                    // if opening app, clear out old items
                    if(maxDate.equals(new Date(0))) {
                        adapter.clear();
                    }

                    mPosts.addAll(posts);
                    adapter.notifyDataSetChanged();

                    // For logging purposes
                    for (int i = 0; i < posts.size(); i++) {
                        Log.d(TAG, "Post[" + i + "] = "
                                + posts.get(i).getDescription()
                                + "\nusername = " + posts.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get maximum Date to find next post to load.
    protected Date getMaxDate() {
        int size = mPosts.size();
        if (size == 0) {
            return new Date(0);
        } else {
            Post oldest = mPosts.get(mPosts.size() - 1);
            return oldest.getCreatedAt();
        }
    }
}

