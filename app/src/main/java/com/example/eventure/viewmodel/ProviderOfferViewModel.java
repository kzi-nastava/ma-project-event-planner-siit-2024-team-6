package com.example.eventure.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.Offer;
import com.example.eventure.paging.ProviderOfferDataSource;
import com.example.eventure.paging.ProviderOfferDataSourceFactory;

public class ProviderOfferViewModel extends ViewModel {

    private LiveData<PagedList<Offer>> pagedOffers;
    private ProviderOfferDataSourceFactory dataSourceFactory;
    private PagedList.Config config;

    public ProviderOfferViewModel(int providerId, int pageSize) {
        // Initialize DataSourceFactory and PagedList
        dataSourceFactory = new ProviderOfferDataSourceFactory(providerId, pageSize, "");
        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .build();
        pagedOffers = new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<Offer>> getPagedOffers() {
        return pagedOffers;
    }

    /**
     * Triggers a search query and refreshes the data.
     *
     * @param query The search query to filter offers.
     */
    public void searchOffers(String query) {
        Log.d("ProviderOfferViewModel", "searchOffers called with query: " + query);
        if (dataSourceFactory != null) {
            // Update the query and invalidate the DataSource
            dataSourceFactory.setQuery(query);
            refresh();
        } else {
            Log.e("ProviderOfferViewModel", "DataSourceFactory is null");
        }
    }

    /**
     * Refreshes the current data source to fetch updated data.
     */
    public void refresh() {
        if (pagedOffers != null && pagedOffers.getValue() != null) {
            Log.d("ProviderOfferViewModel", "Refreshing data...");
            // Invalidate the data source and rebuild LiveData
            dataSourceFactory.getCurrentDataSource().invalidate();
            pagedOffers = new LivePagedListBuilder<>(dataSourceFactory, config).build();
        } else {
            Log.e("ProviderOfferViewModel", "PagedList is not initialized. Cannot refresh.");
        }
    }
}
