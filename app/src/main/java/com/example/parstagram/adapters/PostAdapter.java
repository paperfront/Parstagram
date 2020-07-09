package com.example.parstagram.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostGridBinding;
import com.example.parstagram.fragments.CommentsFragment;
import com.example.parstagram.fragments.PostDetailFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.helpers.ParseRelativeDate;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        private TextView tvLikes;
        private Button btComment;
        private Button btLike;
        private boolean isLiked = false;

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
            btComment = binding.btComment;
            tvLikes = binding.tvLikes;
            btLike = binding.btLike;
        }

        @SuppressLint("ClickableViewAccessibility")
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
            tvLikes.setText(currentPost.getTotalLikes() + " likes");


            View.OnClickListener profileListener = new View.OnClickListener() {
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
            };

            ivProfilePicture.setOnClickListener(profileListener);

            tvUsername.setOnClickListener(profileListener);

            tvComments.setText("View " + currentPost.getTotalComments() + " Comments");

            View.OnClickListener commentsListener = new View.OnClickListener() {
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
            };

            btComment.setOnClickListener(commentsListener);
            tvComments.setOnClickListener(commentsListener);

            ivMainPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PostDetailFragment fragment = PostDetailFragment.newInstance(currentPost);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            final User currentUser = (User) ParseUser.getCurrentUser();
            try {
                if (currentUser.likesPost(currentPost)) {
                    setToLiked();
                } else {
                    setToUnliked();
                }
            } catch (ParseException e) {
                Log.e(TAG, "Failed to load liked posts relation.", e);
            }

            btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleLikeAction(currentPost);
                }
            });

            ivMainPicture.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TEST", "onDoubleTap");
                        handleLikeAction(currentPost);
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

        }


        private void setToLiked() {
            Drawable filledHeart = context.getDrawable(R.mipmap.ufi_heart_active);
            DrawableCompat.setTint(filledHeart, Color.RED);
            btLike.setBackground(filledHeart);
            isLiked = true;
        }

        private void setToUnliked() {
            Drawable filledHeart = context.getDrawable(R.mipmap.ufi_heart);
            btLike.setBackground(filledHeart);
            isLiked = false;
        }

        private void handleLikeAction(final Post currentPost) {
            final User currentUser = (User) ParseUser.getCurrentUser();
            if (isLiked) {
                Log.i(TAG, "Uniking post...");
                currentUser.removePost(currentPost);
                currentPost.decrementLikes();
                setToUnliked();
            } else {
                Log.i(TAG, "Liking post...");
                currentUser.addPost(currentPost);
                currentPost.incrementLikes();
                setToLiked();
            }
            currentPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    tvLikes.setText(currentPost.getTotalLikes() + " likes");
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
        void bind(final Post post) {
            ImageUtils.loadIntoGrid(context, post.getImage(), ivMainPicture);
            ivMainPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PostDetailFragment fragment = PostDetailFragment.newInstance(post);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
