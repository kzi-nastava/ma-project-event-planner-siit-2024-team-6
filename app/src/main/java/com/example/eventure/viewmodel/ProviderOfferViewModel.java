package com.example.eventure.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.eventure.model.Offer;
import com.example.eventure.paging.ProviderOfferDataSourceFactory;

public class ProviderOfferViewModel extends ViewModel {

    private LiveData<PagedList<Offer>> pagedOffers;
    private ProviderOfferDataSourceFactory dataSourceFactory;

    public ProviderOfferViewModel(int providerId, int pageSize) {
        dataSourceFactory = new ProviderOfferDataSourceFactory(providerId, pageSize);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();
        pagedOffers = new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<Offer>> getPagedOffers() {
        return pagedOffers;
    }

    public void refresh() {
        dataSourceFactory.getLiveData().getValue().invalidate();
    }
}
