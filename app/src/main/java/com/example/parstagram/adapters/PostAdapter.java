package com.example.parstagram.adapters;

import android.content.ClipData;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.ImageUtils;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.models.Post;

import java.util.List;

public class PostAdapter extends PagedListAdapter<Post, PostAdapter.ViewHolder> {

    private Context context;
    private ItemPostBinding binding;

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

    public PostAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPostBinding.bind(itemView);
            ivProfilePicture = binding.ivProfilePicture;
            ivMainPicture = binding.ivMainImage;
            tvUsername = binding.tvUsername;
            tvDescription = binding.tvDescription;
        }

        private void bind(Post currentPost) {
            ImageUtils.loadImages(currentPost.getImage(), ivMainPicture);
            ivProfilePicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground));
            String descriptionString = "<b>" + currentPost.getAuthor().getUsername() + "</b> : " + currentPost.getDescription();
            tvDescription.setText(Html.fromHtml(descriptionString));
            tvUsername.setText(currentPost.getAuthor().getUsername());
        }
    }
}
