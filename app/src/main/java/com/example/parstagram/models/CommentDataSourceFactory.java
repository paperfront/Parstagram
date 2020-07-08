package com.example.parstagram.models;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class CommentDataSourceFactory extends DataSource.Factory<Integer, Comment> {

    // Use to hold a reference to the
    public MutableLiveData<CommentPositionalDataSource> postLiveData;

    private Post post;

    public CommentDataSourceFactory(Post post) {
        this.post = post;
    }

    @Override
    public DataSource<Integer, Comment> create() {
        CommentPositionalDataSource source = new CommentPositionalDataSource(post);
        // Keep reference to the data source with a MutableLiveData reference
        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(source);
        return source;
    }
}
