package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragments.ProfileFragment;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    private Context context;
    private List<Post> posts;
    private FragmentManager fragmentManager;

    public PostsAdapter(Context context, List<Post> posts, FragmentManager fragmentManager) {
        this.context = context;
        this.posts = posts;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvUsername;
        public ImageView ivImage;
        public TextView tvDescription;
        public ImageView ivProfileImage;
        public TextView tvDetailTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvDetailTimeStamp = itemView.findViewById(R.id.tvCreationTime);

            itemView.setOnClickListener(this);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("homeScreenBundle", "not null");
                    bundle.putParcelable("username", post.getUser());
                    bundle.putParcelable("image", post.getUser().getParseFile("profileImage"));
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("homeScreenBundle", "not null");
                    bundle.putParcelable("username", post.getUser());
                    bundle.putParcelable("image", post.getUser().getParseFile("profileImage"));
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });

        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            ParseFile profileImage = post.getUser().getParseFile("profileImage");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            if (profileImage == null){
                Glide.with(context).load(R.drawable.instagram_user_outline_24).into(ivProfileImage);
            }
            else  {
                Glide.with(context).load(profileImage.getUrl()).into(ivProfileImage);
            }
            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvDetailTimeStamp.setText(timeAgo);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "Success with Clicking for Detail View");
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

}
