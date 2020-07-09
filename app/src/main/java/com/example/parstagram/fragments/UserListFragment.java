package com.example.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.R;
import com.example.parstagram.adapters.UsersAdapter;
import com.example.parstagram.databinding.FragmentUserListBinding;
import com.example.parstagram.models.User;
import com.example.parstagram.models.UserInfo;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserListFragment extends Fragment {


    public static final String TAG = "UserListFragment";

    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_FOLLOWERS = "followers";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "user";
    private static final String ARG_PARAM2 = "setting";
    private FragmentUserListBinding binding;
    private RecyclerView rvUsers;
    private UsersAdapter adapter;
    // TODO: Rename and change types of parameters
    private User user;
    private String setting;
    private List<User> users;

    public UserListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment UserListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserListFragment newInstance(User user, String setting) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, user);
        args.putString(ARG_PARAM2, setting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_PARAM1);
            setting = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUserListBinding.bind(view);
        bindElements();
        setupElements();

    }

    private void setupElements() {
        setupRV();
    }

    private void setupRV() {
        users = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        adapter = new UsersAdapter(getContext(), getActivity(), users);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(manager);
        switch (setting) {
            case KEY_FOLLOWERS:
                getFollowers();
                break;
            case KEY_FOLLOWING:
                getFollowing();
                break;
            default:
                Log.e(TAG, "Setting is invalid.");
                return;
        }


    }

    private void getFollowers() {
        user.getUserInfo().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                UserInfo info = (UserInfo) object;
                info.getFollowersRelation().getQuery().findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> objects, ParseException e) {
                        users.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void getFollowing() {
        user.getUserInfo().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                UserInfo info = (UserInfo) object;
                info.getFollowingRelation().getQuery().findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> objects, ParseException e) {
                        users.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void bindElements() {
        rvUsers = binding.rvUsers;
    }
}