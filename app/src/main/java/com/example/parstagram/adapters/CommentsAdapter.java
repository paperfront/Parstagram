package com.example.parstagram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemCommentBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.helpers.ParseRelativeDate;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends PagedListAdapter<Comment, CommentsAdapter.ViewHolder> {

    private Context context;
    private FragmentActivity activity;

    public static final String TAG = "CommentsAdapter";

    public static final DiffUtil.ItemCallback<Comment> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Comment>() {
                @Override
                public boolean areItemsTheSame(Comment oldItem, Comment newItem) {
                    return oldItem.getObjectId() == newItem.getObjectId();
                }
                @Override
                public boolean areContentsTheSame(Comment oldItem, Comment newItem) {
                    return false;
                }
            };

    public CommentsAdapter(){
        super(DIFF_CALLBACK);
    }

    public CommentsAdapter(Context context, FragmentActivity activity) {
        super(DIFF_CALLBACK);

        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = getItem(position);
        if (comment == null) {
            return;
        }
        holder.bind(comment);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCommentBinding binding;

        private ImageView ivProfileImage;
        private TextView tvComment;
        private TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCommentBinding.bind(itemView);
            ivProfileImage = binding.ivProfilePicture;
            tvComment = binding.tvComment;
            tvTimestamp = binding.tvTimestamp;
        }

        private void bind(final Comment comment) {
            ImageUtils.loadProfile(context, comment.getCommenter().getProfilePicture(), ivProfileImage);
            String descriptionString = "<b>" + comment.getCommenter().getUsername() + "</b> : "
                    + comment.getContent();
            tvComment.setText(Html.fromHtml(descriptionString));
            tvTimestamp.setText(ParseRelativeDate.getRelativeTimeAgo(comment.getCreatedAt().toString()));
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment fragment = ProfileFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProfileFragment.KEY_PROFILE, comment.getCommenter());
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
