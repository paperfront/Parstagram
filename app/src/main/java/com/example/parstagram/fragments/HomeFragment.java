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

import com.example.parstagram.R;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.databinding.FragmentHomeBinding;
import com.example.parstagram.models.ParseDataSourceFactory;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView rvFeed;
    private SwipeRefreshLayout swipeContainer;
    private LiveData<PagedList<Post>> posts;
    private PostAdapter adapter;


    public static final String TAG = "HomeFragment";
    private ParseDataSourceFactory factory;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);

        bindElements();
        setupElements();
    }

    private void bindElements() {
        rvFeed = binding.rvFeed;
        swipeContainer = binding.swipeContainer;
    }

    private void setupElements() {
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

    private void setupRV() {
        adapter = new PostAdapter(getContext(), getActivity(), PostAdapter.TYPE_FEED);
        setupData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvFeed.setAdapter(adapter);
        rvFeed.setLayoutManager(layoutManager);
    }

    private void setupData() {
        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder().setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10).build();
        factory = new ParseDataSourceFactory();

        posts = new LivePagedListBuilder(factory, pagedListConfig).build();
        swipeContainer.setRefreshing(true);
        posts.observe(getActivity(), new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable PagedList<Post> postList) {
                adapter.submitList(postList);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                swipeContainer.setRefreshing(false);
                if (e != null) {
                    Log.e(TAG, "Error with query", e);
                } else {
                    Log.i(TAG, "Query successful.");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}