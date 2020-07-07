package com.example.parstagram.adapters;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.ImageUtils;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {


    private List<Post> posts;
    private Context context;
    private ItemPostBinding binding;


    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
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
            tvDescription.setText(currentPost.getDescription());
            tvUsername.setText(currentPost.getAuthor().getUsername());
        }
    }
}
