package com.example.parstagram.models;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class CommentPositionalDataSource extends PositionalDataSource<Comment> {

    private Post currentPost;

    public CommentPositionalDataSource(Post currentPost) {
        this.currentPost = currentPost;
    }

    // define basic query here
    public ParseQuery<Comment> getQuery() {
        return ParseQuery.getQuery(Comment.class)
                .whereEqualTo(Comment.KEY_POST, currentPost)
                .orderByDescending("createdAt")
                .include(Comment.KEY_POST)
                .include(Comment.KEY_COMMENTER);

    }

    @Override
    public void loadInitial(@NonNull PositionalDataSource.LoadInitialParams params, @NonNull PositionalDataSource.LoadInitialCallback<Comment> callback) {
        // get basic query
        ParseQuery<Comment> query = getQuery();

        // Use values passed when PagedList was created.
        query.setLimit(params.requestedLoadSize);
        query.setSkip(params.requestedStartPosition);

        try {
            // loadInitial() should run queries synchronously so the initial list will not be empty
            // subsequent fetches can be async
            int count = query.count();
            List<Comment> comments = query.find();

            // return info back to PagedList
            callback.onResult(comments, params.requestedStartPosition, count);
        } catch (ParseException e) {
            // retry logic here
        }
    }

    @Override
    public void loadRange(@NonNull PositionalDataSource.LoadRangeParams params, @NonNull PositionalDataSource.LoadRangeCallback<Comment> callback) {
        // get basic query
        ParseQuery<Comment> query = getQuery();

        query.setLimit(params.loadSize);

        // fetch the next set from a different offset
        query.setSkip(params.startPosition);

        try {
            // run queries synchronously since function is called on a background thread
            List<Comment> comments = query.find();

            // return info back to PagedList
            callback.onResult(comments);
        } catch (ParseException e) {
            // retry logic here
        }
    }

}
