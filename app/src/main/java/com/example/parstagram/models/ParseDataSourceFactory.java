package com.example.parstagram.models;

import androidx.paging.DataSource;

public class ParseDataSourceFactory extends DataSource.Factory<Integer, Post> {

    @Override
    public DataSource<Integer, Post> create() {
        ParsePositionalDataSource source = new ParsePositionalDataSource();
        return source;
    }
}
