package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentCommentsBinding;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {


    public static final String TAG = "CommentsFragment";
    public static final String KEY_POST = "post";

    private RecyclerView rvComments;
    private Button btPost;
    private EditText etComment;

    private FragmentCommentsBinding binding;

    private Post currentPost;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentsFragment newInstance() {
        CommentsFragment fragment = new CommentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentPost = getArguments().getParcelable(KEY_POST);
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCommentsBinding.bind(view);
        bindElements();
        setupElements();
    }



    private void bindElements() {
        etComment = binding.etComment;
        btPost = binding.btPost;
        rvComments = binding.rvComments;
    }

    private void setupElements() {
        setupButtons();
        setupRV();
    }

    private void setupButtons() {
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(getContext(), "You must enter a comment before submitting.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveComment(comment);
                }
            }
        });
    }

    private void saveComment(String comment) {
        Comment newComment = new Comment();
        newComment.setContent(comment);
        newComment.setCommenter((User) ParseUser.getCurrentUser());
        newComment.setPost(currentPost);
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to save comment", e);
                    Toast.makeText(getContext(), "Failed to save comment", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Successfully saved comment.");
                    currentPost.incrementComments();
                    Toast.makeText(getContext(), "Successfully made comment!", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                }
            }
        });
    }

    private void setupRV() {

    }
}