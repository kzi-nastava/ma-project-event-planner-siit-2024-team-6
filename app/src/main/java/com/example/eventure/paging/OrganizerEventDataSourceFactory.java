package com.example.eventure.paging;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.Event;

public class OrganizerEventDataSourceFactory extends DataSource.Factory<Integer, Event> {

    private final int pageSize;
    private String query;
    private final MutableLiveData<OrganizerEventDataSource> dataSourceLiveData = new MutableLiveData<>();
    private OrganizerEventDataSource currentDataSource;

    public OrganizerEventDataSourceFactory(int pageSize, String query) {
        this.pageSize = pageSize;
        this.query = query;
    }

    @Override
    public DataSource<Integer, Event> create() {
        currentDataSource = new OrganizerEventDataSource(pageSize, query);
        dataSourceLiveData.postValue(currentDataSource);
        return currentDataSource;
    }

    public void setQuery(String query) {
        Log.d("OrganizerEventDataSourceFactory", "setQuery: " + query);
        this.query = query;
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }

    public MutableLiveData<OrganizerEventDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }

    public OrganizerEventDataSource getCurrentDataSource() {
        return currentDataSource;
    }
}
