package com.example.eventure.paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.Offer;

public class ProviderProductDataSourceFactory extends DataSource.Factory<Integer, Offer> {

    private final int pageSize;
    private String query, category, eventType;
    private Boolean onSale, isAvailable, isFilter;
    private Double price;

    private ProviderProductDataSource currentDataSource;
    private final MutableLiveData<ProviderProductDataSource> dataSourceLiveData = new MutableLiveData<>();

    public ProviderProductDataSourceFactory(int pageSize, String query, String category,
                                            String eventType, Double price, Boolean onSale,
                                            Boolean isAvailable, boolean isFilter) {
        this.pageSize = pageSize;
        this.query = query;
        this.category = category;
        this.eventType = eventType;
        this.price = price;
        this.onSale = onSale;
        this.isAvailable = isAvailable;
        this.isFilter = isFilter;
    }

    @Override
    public DataSource<Integer, Offer> create() {
        currentDataSource = new ProviderProductDataSource(
                pageSize, query, category, eventType,
                onSale, isAvailable, price, isFilter);
        dataSourceLiveData.postValue(currentDataSource);
        return currentDataSource;
    }

    public void setQuery(String query) {
        this.query = query;
        this.isFilter = false;
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }

    public void setFilters(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        this.category = category;
        this.eventType = eventType;
        this.onSale = onSale;
        this.isAvailable = isAvailable;
        this.price = price;
        this.isFilter = true;
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        }
    }

    public ProviderProductDataSource getCurrentDataSource() {
        return currentDataSource;
    }

    public MutableLiveData<ProviderProductDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
