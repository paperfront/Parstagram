package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
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
    private ItemPostBinding binding;
    private FragmentActivity activity;

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

    public PostAdapter(Context context, FragmentActivity activity) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = getItem(position);
        if (post == null) {
            return;
        }
        holder.bind(post);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView ivProfilePicture;
        private ImageView ivMainPicture;
        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvComments;
        private TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPostBinding.bind(itemView);
            ivProfilePicture = binding.ivProfilePicture;
            ivMainPicture = binding.ivMainImage;
            tvUsername = binding.tvUsername;
            tvDescription = binding.tvDescription;
            tvComments = binding.tvComments;
            tvTimestamp = binding.tvTimestamp;
        }
        private void bind(final Post currentPost) {
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

        }
    }
}
