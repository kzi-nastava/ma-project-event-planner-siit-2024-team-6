package com.example.eventure.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.Event;
import com.example.eventure.paging.OrganizerEventDataSourceFactory;

public class OrganizerEventViewModel extends ViewModel {

    private LiveData<PagedList<Event>> pagedEvents;
    private OrganizerEventDataSourceFactory dataSourceFactory;
    private PagedList.Config config;
    private final int pageSize;

    public OrganizerEventViewModel(int pageSize) {
        this.pageSize = pageSize;
        initFactoryAndPagedList("");
    }

    private void initFactoryAndPagedList(String query) {
        dataSourceFactory = new OrganizerEventDataSourceFactory(pageSize, query);

        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .build();

        pagedEvents = new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<Event>> getPagedEvents() {
        return pagedEvents;
    }
    public void refresh() {
        if (dataSourceFactory.getCurrentDataSource() != null) {
            dataSourceFactory.getCurrentDataSource().invalidate();
        }
    }

    public void searchEvents(String query) {
        dataSourceFactory.setQuery(query); // обновляем запрос
        refresh(); // триггерим перезагрузку
    }

}
