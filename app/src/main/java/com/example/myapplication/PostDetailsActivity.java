package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragments.ProfileFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
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
    ImageView ivProfileImageMedia;
    ImageButton imbtnIsLikedDetail;
    TextView tvLikesCountDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvDetailUser = findViewById(R.id.tvDetailUser);
        tvDetailTimeStamp = findViewById(R.id.tvDetailTimeStamp);
        tvDetailComment = findViewById(R.id.tvDetailComment);
        ivDetailMedia = findViewById(R.id.ivDetailMedia);
        ivProfileImageMedia = findViewById(R.id.ivProfileImageDetail);
        imbtnIsLikedDetail = findViewById(R.id.isLikedDetail);
        tvLikesCountDetail = findViewById(R.id.tvDetailLikesCount);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", post.getDescription()));

        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvDetailUser.setText(post.getUser().getUsername());
        tvDetailComment.setText(post.getDescription());
        tvDetailTimeStamp.setText(timeAgo);
        tvLikesCountDetail.setText(post.getLikesCount() + " likes");
        if (post.getIsLiked()){
            imbtnIsLikedDetail.setImageResource(R.drawable.ic_vector_heart);
            imbtnIsLikedDetail.setColorFilter(Color.parseColor("#ffe0245e"));
        }
        else{
            imbtnIsLikedDetail.setImageResource(R.drawable.ic_vector_heart_stroke);
            imbtnIsLikedDetail.setColorFilter(Color.parseColor("#000000"));
        }

        imbtnIsLikedDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // user is liking tweet
                if (!post.getIsLiked()) {
                    imbtnIsLikedDetail.setImageResource(R.drawable.ic_vector_heart);
                    imbtnIsLikedDetail.setColorFilter(Color.parseColor("#ffe0245e"));
                    long likeCount = post.getLikesCount();

                    post.setIsLiked(true);
                    post.setNumLikes(likeCount + 1);
                    post.likePost(ParseUser.getCurrentUser());
                    post.saveInBackground();
                }
                // user is unliking tweet
                else {
                    imbtnIsLikedDetail.setImageResource(R.drawable.ic_vector_heart_stroke);
                    imbtnIsLikedDetail.setColorFilter(Color.parseColor("#000000"));

                    long likeCount = post.getLikesCount();
                    post.setIsLiked(false);
                    try {
                        post.unLikePost(ParseUser.getCurrentUser(), post.getLikedUsers());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    post.setNumLikes(likeCount - 1);
                    post.saveInBackground();

                }
                tvLikesCountDetail.setText(post.getLikesCount() + " likes");
            }
        });

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivDetailMedia);
        }

        ParseFile profileImage = post.getUser().getParseFile("profileImage");
        if (profileImage == null){
            Glide.with(this).load(R.drawable.instagram_user_outline_24).into(ivProfileImageMedia);
        }
        else  {
            Glide.with(this).load(profileImage.getUrl()).into(ivProfileImageMedia);
        }

    }

}