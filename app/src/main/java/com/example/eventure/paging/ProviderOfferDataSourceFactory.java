package com.example.eventure.paging;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.Offer;

import java.util.List;

public class ProviderOfferDataSourceFactory extends DataSource.Factory<Integer, Offer> {

    private int providerId;
    private int pageSize;
    private String query, category, eventType;
    private Double price;
    private Boolean isAvailable, isFilter, onSale;
    private MutableLiveData<ProviderOfferDataSource> dataSourceLiveData;
    private ProviderOfferDataSource currentDataSource;

    public ProviderOfferDataSourceFactory(int pageSize, String query, String c, String et, Double price, Boolean s, Boolean a, boolean f) {
        this.pageSize = pageSize;
        this.query = query;
        this.category = c;
        this.eventType = et;
        this.onSale = s;
        this.isAvailable = a;
        this.price = price;
        this.isFilter = f;
        dataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, Offer> create() {
        // Create a new instance of the data source
        currentDataSource = new ProviderOfferDataSource(pageSize, query, category, eventType, onSale, isAvailable, price, isFilter);
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
        this.isFilter = false;

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

    public void setFilters(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        this.category = category;
        this.eventType = eventType;
        this.onSale = onSale;
        this.isAvailable = isAvailable;
        this.price = price;
        this.isFilter = true;

        // Invalidate the current data source to force a reload with the new query
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }


}
