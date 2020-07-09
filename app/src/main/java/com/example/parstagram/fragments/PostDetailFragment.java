package com.example.parstagram.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentPostDetailBinding;
import com.example.parstagram.helpers.ImageUtils;
import com.example.parstagram.helpers.ParseRelativeDate;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailFragment extends Fragment {

    private static final String ARG_POST = "post";
    public static final String TAG = "PostDetailFragment";

    private Post post;

    private FragmentPostDetailBinding binding;

    private ImageView ivProfilePicture;
    private ImageView ivMainPicture;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvComments;
    private TextView tvTimestamp;
    private TextView tvLikes;
    private Button btComment;
    private Button btLike;
    private ImageView ivPopupHeart;
    private boolean isLiked = false;

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
        btLike = binding.post.btLike;
        tvLikes = binding.post.tvLikes;
        ivPopupHeart = binding.post.ivPopupHeart;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupElements() {
        if (!post.getAuthor().has(User.KEY_PROFILE_PICTURE)) {
            ImageUtils.loadDefaultProfilePic(getContext(), ivProfilePicture);
        } else {
            ImageUtils.loadProfile(getContext(), post.getAuthor().getProfilePicture(),
                    ivProfilePicture);
        }
        ivPopupHeart.setVisibility(View.GONE);
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
        updateLikes();

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

        final User currentUser = (User) ParseUser.getCurrentUser();
        try {
            if (currentUser.likesPost(post)) {
                setToLiked();
            }
        } catch (ParseException e) {
            Log.e(TAG, "Failed to load liked posts relation.", e);
        }
        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLikeAction();
            }
        });

        ivMainPicture.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    ivPopupHeart.setVisibility(View.VISIBLE);
                    Drawable fullHeart = getContext().getDrawable(R.mipmap.ufi_heart_active);
                    DrawableCompat.setTint(fullHeart, Color.RED);
                    ivPopupHeart.setBackground(fullHeart);
                    YoYo.with(Techniques.Landing)
                            .duration(500)
                            .playOn(ivPopupHeart);
                    YoYo.with(Techniques.TakingOff)
                            .duration(500)
                            .playOn(ivPopupHeart);
                    handleLikeAction();
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

    private void handleLikeAction() {
        final User currentUser = (User) ParseUser.getCurrentUser();
        if (isLiked) {
            Log.i(TAG, "Uniking post...");
            currentUser.removePost(post);
            post.decrementLikes();
            setToUnliked();
        } else {
            Log.i(TAG, "Liking post...");
            currentUser.addPost(post);
            post.incrementLikes();
            setToLiked();
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                updateLikes();
            }
        });
    }

    private void updateLikes() {
        tvLikes.setText(post.getTotalLikes() + " likes");
    }

    private void setToLiked() {
        Drawable filledHeart = getContext().getDrawable(R.mipmap.ufi_heart_active);
        DrawableCompat.setTint(filledHeart, Color.RED);
        btLike.setBackground(filledHeart);
        isLiked = true;
    }

    private void setToUnliked() {
        Drawable filledHeart = getContext().getDrawable(R.mipmap.ufi_heart);
        btLike.setBackground(filledHeart);
        isLiked = false;
    }
}