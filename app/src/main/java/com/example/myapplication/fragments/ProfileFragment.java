package com.example.myapplication.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.Post;
import com.example.myapplication.PostsAdapter;
import com.example.myapplication.ProfileAdapter;
import com.example.myapplication.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private Button btnCaptureImage;
    private String photoFileName = "photo.jpg";
    private File photoFile;


    public static final String TAG = "ProfileFragment";
    private SwipeRefreshLayout swipeContainer;
    protected RecyclerView rvPosts;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    private TextView tvProfileUsername;
    private ImageView ivProfileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        tvProfileUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnCaptureImage = view.findViewById(R.id.btnProfileImage);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        ParseFile profileImage = null;

        try{
            if (getArguments().get("homeScreenBundle").equals("not null")) {
            Log.i(TAG, "user clicked on someone's profile from the home screen");
            ParseUser user = (ParseUser) getArguments().get("username");
            tvProfileUsername.setText(user.getUsername());
            profileImage = (ParseFile) getArguments().get("image");
        }}
        catch (Exception e) {
            e.printStackTrace();
                Log.i(TAG, "user did not click on someone's profile from the home screen");
                tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());
                profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        }

        if (profileImage == null){
            Glide.with(this).load(R.drawable.instagram_user_outline_24).into(ivProfileImage);
        }
        else  {
            Glide.with(this).load(profileImage.getUrl()).into(ivProfileImage);
        }

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
               // ParseUser currentUser = ParseUser.getCurrentUser();
                //saveProfilePicture(photoFile);
            }
        });

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), GridLayoutManager.chooseSize(3,3,3)));

        // query posts from Parstagram
        queryPosts();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        try{
            if (getArguments().get("homeScreenBundle").equals("not null")) {
                query.whereEqualTo(Post.KEY_USER, getArguments().get("username"));
                ParseUser user = (ParseUser) getArguments().get("username");
                if(!ParseUser.getCurrentUser().getUsername().equals(user.getUsername())){
                    btnCaptureImage.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        }

        query.setLimit(20);

        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts for profile view", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                saveProfilePicture(takenImage);
                ivProfileImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName){
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // updates user's profile image to parse
    private void saveProfilePicture(Bitmap photoFile) {
        Log.d(TAG, String.valueOf(photoFile));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photoFile.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);

        ParseFile newProfileImage = new ParseFile(byteArrayOutputStream.toByteArray());
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("profileImage", newProfileImage);

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving post!", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                ivProfileImage.setImageBitmap(photoFile);
            }
        });
    }

}
