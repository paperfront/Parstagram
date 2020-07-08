package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.parstagram.adapters.CommentsAdapter;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.databinding.FragmentCommentsBinding;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.CommentDataSourceFactory;
import com.example.parstagram.models.ParseDataSourceFactory;
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
    private SwipeRefreshLayout swipeContainer;

    private FragmentCommentsBinding binding;

    private Post currentPost;
    private LiveData<PagedList<Comment>> comments;
    private CommentsAdapter adapter;
    private CommentDataSourceFactory factory;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CommentsFragment.
     */
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
        swipeContainer = binding.swipeContainer;
    }

    private void setupElements() {
        setupButtons();
        setupRV();
        setupSwipeContainer();
    }

    private void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                factory.postLiveData.getValue().invalidate();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupData() {
        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder().setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(15).build();
        factory = new CommentDataSourceFactory(currentPost);

        comments = new LivePagedListBuilder(factory, pagedListConfig).build();
        comments.observe(getActivity(), new Observer<PagedList<Comment>>() {
            @Override
            public void onChanged(@Nullable PagedList<Comment> commentList) {
                adapter.submitList(commentList);
                swipeContainer.setRefreshing(false);
            }
        });
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
                    factory.postLiveData.getValue().invalidate();
                }
            }
        });
    }

    private void setupRV() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        adapter = new CommentsAdapter(getContext(), getActivity());
        setupData();
        rvComments.setLayoutManager(manager);
        rvComments.setAdapter(adapter);
    }
}