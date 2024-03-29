package com.example.instagram.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.Activities.HomeActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

public class PostFragment extends Fragment {

    private final String TAG = "ComposeFragment";

    private EditText caption;
    private TextView captureImage;
    private ImageView cancel;
    private ImageView postImage;
    private TextView share;

    private Boolean wasPictureTaken;

    // Instance variables for camera
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wasPictureTaken = false;

        // Find references for the views
        caption = view.findViewById(R.id.caption);
        captureImage = view.findViewById(R.id.tvCaptureImage);
        postImage = view.findViewById(R.id.postImage);
        share = view.findViewById(R.id.tvShare);
        cancel = view.findViewById(R.id.ivCancel);

        // Set listener for capture image button
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        //Set listener for cancel post creation button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Query posts
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that the user has taken a photo
                if (wasPictureTaken) {
                    String description = caption.getText().toString();
                    ParseUser user = ParseUser.getCurrentUser();
                    savePost(description, user, photoFile);
                }
                else {
                    Log.e(TAG, "No photo to submit.");
                    Toast.makeText(getContext(), "Take a photo first!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Save a Post with given description and user
    private void savePost(String description, ParseUser user, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(photoFile));
        post.setLikes(new ArrayList<ParseUser>(0));

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Post saved successfully!");

                    // Refresh to load new post in home
                    getFragmentManager().beginTransaction()
                            .replace(R.id.centerView, new TimelineFragment(), TimelineFragment.TAG)
                            .commit();
                } else {
                    Log.e(TAG, "Error while saving.");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                postImage.setImageBitmap(takenImage);
                this.wasPictureTaken = true;
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                this.wasPictureTaken = false;
            }
        }
    }
}
