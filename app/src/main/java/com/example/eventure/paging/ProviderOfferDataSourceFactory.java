package com.example.eventure.paging;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.Offer;

public class ProviderOfferDataSourceFactory extends DataSource.Factory<Integer, Offer> {

    private int providerId;
    private int pageSize;
    private String query;
    private MutableLiveData<ProviderOfferDataSource> dataSourceLiveData;
    private ProviderOfferDataSource currentDataSource;

    public ProviderOfferDataSourceFactory(int providerId, int pageSize, String query) {
        this.providerId = providerId;
        this.pageSize = pageSize;
        this.query = query;
        dataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, Offer> create() {
        // Create a new instance of the data source
        currentDataSource = new ProviderOfferDataSource(providerId, pageSize, query);
        // Post the current data source for observation
        dataSourceLiveData.postValue(currentDataSource);
        return currentDataSource;
    }

    /**
     * Updates the query and invalidates the current data source to trigger a refresh.
     *
     * @param query The new search query.
     */
    public void setQuery(String query) {
        Log.d("ProviderOfferDataSourceFactory", "setQuery called with: " + query);
        this.query = query;

        // Invalidate the current data source to force a reload with the new query
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }

    /**
     * Returns the live data for observing changes to the data source.
     *
     * @return MutableLiveData for the current data source.
     */
    public MutableLiveData<ProviderOfferDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
    public ProviderOfferDataSource getCurrentDataSource() {
        return currentDataSource;
    }

}
