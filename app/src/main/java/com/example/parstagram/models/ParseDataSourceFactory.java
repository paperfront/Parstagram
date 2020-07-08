package com.example.parstagram.models;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

public class ParseDataSourceFactory extends DataSource.Factory<Integer, Post> {

    // Use to hold a reference to the
    public MutableLiveData<ParsePositionalDataSource> postLiveData;

    @Override
    public DataSource<Integer, Post> create() {
        ParsePositionalDataSource source = new ParsePositionalDataSource();
        // Keep reference to the data source with a MutableLiveData reference
        postLiveData = new MutableLiveData<>();
        postLiveData.postValue(source);
        return source;
    }
}
