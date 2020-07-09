package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentPostDetailBinding;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.helpers.ParseRelativeDate;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailFragment extends Fragment {

    private static final String ARG_POST = "post";

    private Post post;

    private FragmentPostDetailBinding binding;

    private ImageView ivProfilePicture;
    private ImageView ivMainPicture;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvComments;
    private TextView tvTimestamp;
    private Button btComment;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PostDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailFragment newInstance(Post param1) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = getArguments().getParcelable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentPostDetailBinding.bind(view);
        bindElements();
        setupElements();
    }

    private void bindElements() {
        ivProfilePicture = binding.post.ivProfilePicture;
        ivMainPicture = binding.post.ivMainImage;
        tvUsername = binding.post.tvUsername;
        tvDescription = binding.post.tvDescription;
        tvComments = binding.post.tvComments;
        tvTimestamp = binding.post.tvTimestamp;
        btComment = binding.post.btComment;
    }

    private void setupElements() {
        if (!post.getAuthor().has(User.KEY_PROFILE_PICTURE)) {
            ImageUtils.loadDefaultProfilePic(getContext(), ivProfilePicture);
        } else {
            ImageUtils.loadProfile(getContext(), post.getAuthor().getProfilePicture(),
                    ivProfilePicture);
        }
        ImageUtils.loadImages(getContext(), post.getImage(), ivMainPicture);
        String descriptionString = "<b>" + post.getAuthor().getUsername() + "</b> : " + post.getDescription();
        tvDescription.setText(Html.fromHtml(descriptionString));
        tvUsername.setText(post.getAuthor().getUsername());
        tvTimestamp.setText(ParseRelativeDate.getRelativeTimeAgo(post.getCreatedAt().toString()));


        View.OnClickListener profileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = ProfileFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelable(ProfileFragment.KEY_PROFILE, post.getAuthor());
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        ivProfilePicture.setOnClickListener(profileListener);
        tvUsername.setOnClickListener(profileListener);

        tvComments.setText("View " + post.getTotalComments() + " Comments");

        View.OnClickListener commentsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentsFragment fragment = CommentsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelable(CommentsFragment.KEY_POST, post);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        btComment.setOnClickListener(commentsListener);
        tvComments.setOnClickListener(commentsListener);
    }
}