package com.example.eventure.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.EventType;
import com.example.eventure.paging.AdminEventTypeDataSourceFactory;

public class AdminEventTypeViewModel extends ViewModel {

    private LiveData<PagedList<EventType>> pagedEventTypes;
    private AdminEventTypeDataSourceFactory dataSourceFactory;
    private PagedList.Config config;
    private final int pageSize;

    public AdminEventTypeViewModel(int pageSize) {
        this.pageSize = pageSize;
        initFactoryAndPagedList("");
    }

    private void initFactoryAndPagedList(String query) {
        dataSourceFactory = new AdminEventTypeDataSourceFactory(pageSize, query);

        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .build();

        pagedEventTypes = new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<EventType>> getPagedEventTypes() {
        return pagedEventTypes;
    }

    public void refresh() {
        if (dataSourceFactory.getCurrentDataSource() != null) {
            dataSourceFactory.getCurrentDataSource().invalidate();
        }
    }

    public void searchEventTypes(String query) {
        dataSourceFactory.setQuery(query);
        refresh();
    }
}
