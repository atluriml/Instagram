package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";
    Post post;
    TextView tvDetailUser;
    TextView tvDetailComment;
    TextView tvDetailTimeStamp;
    ImageView ivDetailMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvDetailUser = findViewById(R.id.tvDetailUser);
        tvDetailTimeStamp = findViewById(R.id.tvDetailTimeStamp);
        tvDetailComment = findViewById(R.id.tvDetailComment);
        ivDetailMedia = findViewById(R.id.ivDetailMedia);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", post.getDescription()));

        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);

        tvDetailUser.setText(post.getUser().getUsername());
        tvDetailComment.setText(post.getDescription());
        tvDetailTimeStamp.setText(timeAgo);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivDetailMedia);
        }

    }

}