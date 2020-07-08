package com.example.parstagram.models;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class ParseGridDataSourceFactory extends DataSource.Factory<Integer, Post> {

    // Use to hold a reference to the
    public MutableLiveData<ParseGridPositionalDataSource> postLiveData;

    private User user;

    public ParseGridDataSourceFactory(User user) {
        this.user = user;
    }

    @Override
    public DataSource<Integer, Post> create() {
        ParseGridPositionalDataSource source = new ParseGridPositionalDataSource(user);
        // Keep reference to the data source with a MutableLiveData reference
        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(source);
        return source;
    }
}
