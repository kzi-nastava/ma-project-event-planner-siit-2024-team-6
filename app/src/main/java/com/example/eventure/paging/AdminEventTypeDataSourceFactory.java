package com.example.eventure.paging;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.EventType;

public class AdminEventTypeDataSourceFactory extends DataSource.Factory<Integer, EventType> {

    private final int pageSize;
    private String query;
    private final MutableLiveData<AdminEventTypeDataSource> dataSourceLiveData = new MutableLiveData<>();
    private AdminEventTypeDataSource currentDataSource;

    public AdminEventTypeDataSourceFactory(int pageSize, String query) {
        this.pageSize = pageSize;
        this.query = query;
    }

    @Override
    public DataSource<Integer, EventType> create() {
        currentDataSource = new AdminEventTypeDataSource(pageSize);
        dataSourceLiveData.postValue(currentDataSource);
        return currentDataSource;
    }

    public void setQuery(String query) {
        Log.d("AdminEventTypeDataSourceFactory", "setQuery: " + query);
        this.query = query;
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }

    public MutableLiveData<AdminEventTypeDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }

    public AdminEventTypeDataSource getCurrentDataSource() {
        return currentDataSource;
    }
}
