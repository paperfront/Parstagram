package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemUserBinding;
import com.example.parstagram.fragments.PostDetailFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private List<User> users;
    private Context context;
    private FragmentActivity activity;

    public UsersAdapter(Context context, FragmentActivity activity, List<User> users) {
        this.context = context;
        this.users = users;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemUserBinding binding;

        private ImageView ivProfilePicture;
        private TextView tvUsername;

        private RelativeLayout rl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUserBinding.bind(itemView);
            rl = binding.getRoot();
            ivProfilePicture = binding.ivProfilePicture;
            tvUsername = binding.tvUsername;
        }

        private void bind(final User user) {
            ImageUtils.loadProfile(context, user.getProfilePicture(), ivProfilePicture);
            tvUsername.setText(user.getUsername());
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment fragment = ProfileFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProfileFragment.KEY_PROFILE, user);
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
