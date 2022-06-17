package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragments.ProfileFragment;
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
    ImageView ivProfileImageMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvDetailUser = findViewById(R.id.tvDetailUser);
        tvDetailTimeStamp = findViewById(R.id.tvDetailTimeStamp);
        tvDetailComment = findViewById(R.id.tvDetailComment);
        ivDetailMedia = findViewById(R.id.ivDetailMedia);
        ivProfileImageMedia = findViewById(R.id.ivProfileImageDetail);

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

        ParseFile profileImage = post.getUser().getParseFile("profileImage");
        if (profileImage == null){
            Glide.with(this).load(R.drawable.instagram_user_outline_24).into(ivProfileImageMedia);
        }
        else  {
            Glide.with(this).load(profileImage.getUrl()).into(ivProfileImageMedia);
        }

//        tvDetailUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("homeScreenBundle", "not null");
//                bundle.putParcelable("username", post.getUser());
//                bundle.putParcelable("image", post.getUser().getParseFile("profileImage"));
//                Fragment fragment = new ProfileFragment();
//                fragment.setArguments(bundle);
//                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
//            }
//        });

    }

}