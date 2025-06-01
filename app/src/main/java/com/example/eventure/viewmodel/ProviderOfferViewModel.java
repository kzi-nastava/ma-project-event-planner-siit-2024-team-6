package com.example.eventure.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.Offer;
import com.example.eventure.paging.ProviderOfferDataSource;
import com.example.eventure.paging.ProviderOfferDataSourceFactory;

import java.util.List;

public class ProviderOfferViewModel extends ViewModel {

    private LiveData<PagedList<Offer>> pagedOffers;
    private ProviderOfferDataSourceFactory dataSourceFactory;
    private PagedList.Config config;

    public ProviderOfferViewModel(int pageSize) {
        // Initialize DataSourceFactory and PagedList
        dataSourceFactory = new ProviderOfferDataSourceFactory( pageSize, "", null, null, null, null, false, false);
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
        if (dataSourceFactory != null) {
            ProviderOfferDataSource currentDataSource = dataSourceFactory.getCurrentDataSource();
            if (currentDataSource != null) {
                currentDataSource.invalidate();
            } else {
                Log.e("ProviderOfferViewModel", "CurrentDataSource is null");
            }
        } else {
            Log.e("ProviderOfferViewModel", "DataSourceFactory is null");
        }
    }

    public void filterOffers(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        Log.d("ProviderOfferViewModel", "filterOffers called with: " +
                "Categories: " + category + ", " +
                "Event Types: " + eventType + ", " +
                "On sale: " + onSale + ", " +
                "Availability: " + isAvailable + ", " +
                "Price: " + price);

        // Update the filters in the data source factory
        dataSourceFactory.setFilters(category, eventType, onSale, isAvailable, price);

        // Refresh the data source to apply the new filters
        refresh();
    }
}
