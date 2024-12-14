package com.example.eventure.paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.eventure.model.Offer;

public class ProviderOfferDataSourceFactory extends DataSource.Factory<Integer, Offer> {

    private MutableLiveData<ProviderOfferDataSource> liveData = new MutableLiveData<>();
    private int providerId;
    private int pageSize;

    public ProviderOfferDataSourceFactory(int providerId, int pageSize) {
        this.providerId = providerId;
        this.pageSize = pageSize;
    }

    @Override
    public DataSource<Integer, Offer> create() {
        ProviderOfferDataSource offerDataSource = new ProviderOfferDataSource(providerId, pageSize);
        liveData.postValue(offerDataSource);
        return offerDataSource;
    }

    public MutableLiveData<ProviderOfferDataSource> getLiveData() {
        return liveData;
    }
}
