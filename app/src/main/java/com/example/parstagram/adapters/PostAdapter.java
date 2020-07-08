package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.databinding.ItemPostGridBinding;
import com.example.parstagram.fragments.CommentsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.helpers.ParseRelativeDate;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseUser;

public class PostAdapter extends PagedListAdapter<Post, PostAdapter.ViewHolder> {

    private Context context;
    private FragmentActivity activity;
    private int holderType;

    public static final int TYPE_FEED = 1;
    public static final int TYPE_GRID = 2;
    public static final String TAG = "PostAdapter";

    public static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Post>() {
                @Override
                public boolean areItemsTheSame(Post oldItem, Post newItem) {
                    return oldItem.getObjectId() == newItem.getObjectId();
                }
                @Override
                public boolean areContentsTheSame(Post oldItem, Post newItem) {
                    return false;
                }
            };

    public PostAdapter(){
        super(DIFF_CALLBACK);
    }

    public PostAdapter(Context context, FragmentActivity activity, int holderType) {
        super(DIFF_CALLBACK);

        this.context = context;
        this.activity = activity;
        this.holderType = holderType;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (holderType) {
            case TYPE_FEED:
                view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
                return new FeedHolder(view);
            case TYPE_GRID:
                view = LayoutInflater.from(context).inflate(R.layout.item_post_grid, parent, false);
                return new GridHolder(view);
            default:
                Log.e(TAG, "Invalid type selected for adapter.");
                view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
                return new FeedHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = getItem(position);
        if (post == null) {
            return;
        }
        holder.bind(post);
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(final Post post);
    }

    public class FeedHolder extends ViewHolder {


        private ImageView ivProfilePicture;
        private ImageView ivMainPicture;
        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvComments;
        private TextView tvTimestamp;

        private ItemPostBinding binding;

        public FeedHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPostBinding.bind(itemView);
            ivProfilePicture = binding.ivProfilePicture;
            ivMainPicture = binding.ivMainImage;
            tvUsername = binding.tvUsername;
            tvDescription = binding.tvDescription;
            tvComments = binding.tvComments;
            tvTimestamp = binding.tvTimestamp;
        }

        void bind(final Post currentPost) {
            if (!currentPost.getAuthor().has(User.KEY_PROFILE_PICTURE)) {
                ImageUtils.loadDefaultProfilePic(context, ivProfilePicture);
            } else {
                ImageUtils.loadProfile(context, currentPost.getAuthor().getProfilePicture(),
                        ivProfilePicture);
            }
            ImageUtils.loadImages(context, currentPost.getImage(), ivMainPicture);
            String descriptionString = "<b>" + currentPost.getAuthor().getUsername() + "</b> : " + currentPost.getDescription();
            tvDescription.setText(Html.fromHtml(descriptionString));
            tvUsername.setText(currentPost.getAuthor().getUsername());
            tvTimestamp.setText(ParseRelativeDate.getRelativeTimeAgo(currentPost.getCreatedAt().toString()));
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment fragment = ProfileFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProfileFragment.KEY_PROFILE, currentPost.getAuthor());
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            tvComments.setText("View " + currentPost.getTotalComments() + " Comments");

            tvComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentsFragment fragment = CommentsFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(CommentsFragment.KEY_POST, currentPost);
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }
    }

    public class GridHolder extends ViewHolder {

        private ImageView ivMainPicture;

        private ItemPostGridBinding binding;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPostGridBinding.bind(itemView);
            ivMainPicture = binding.ivMainImage;
        }

        @Override
        void bind(Post post) {
            ImageUtils.loadIntoGrid(context, post.getImage(), ivMainPicture);
        }
    }
}
