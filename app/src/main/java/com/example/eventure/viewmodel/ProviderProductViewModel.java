package com.example.eventure.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.Offer;
import com.example.eventure.paging.ProviderOfferDataSource;
import com.example.eventure.paging.ProviderOfferDataSourceFactory;
import com.example.eventure.paging.ProviderProductDataSource;
import com.example.eventure.paging.ProviderProductDataSourceFactory;

public class ProviderProductViewModel extends ViewModel {

    private final LiveData<PagedList<Offer>> pagedProducts;
    private final ProviderProductDataSourceFactory dataSourceFactory;
    private final PagedList.Config config;

    public ProviderProductViewModel(int pageSize) {
        // Изначально фильтруем по типу = "product"
        dataSourceFactory = new ProviderProductDataSourceFactory(
                pageSize, "", null, null, null, false, false, false
        );


        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .build();

        pagedProducts = new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<Offer>> getPagedProducts() {
        return pagedProducts;
    }

    public void searchProducts(String query) {
        Log.d("ProviderProductViewModel", "searchProducts called with query: " + query);
        if (dataSourceFactory != null) {
            dataSourceFactory.setQuery(query);
            refresh();
        }
    }

    public void filterProducts(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        Log.d("ProviderProductViewModel", "filterProducts called with: " +
                "Category: " + category + ", EventType: " + eventType +
                ", OnSale: " + onSale + ", Available: " + isAvailable + ", Price: " + price);

        dataSourceFactory.setFilters(category, eventType, onSale, isAvailable, price);
        refresh();
    }

    public void refresh() {
        ProviderProductDataSource currentDataSource = dataSourceFactory.getCurrentDataSource();
        if (currentDataSource != null) {
            currentDataSource.invalidate();
        } else {
            Log.e("ProviderProductViewModel", "CurrentDataSource is null");
        }
    }
}
