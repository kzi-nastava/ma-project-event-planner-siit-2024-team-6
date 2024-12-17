package com.example.eventure.paging;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.eventure.clients.ClientUtils;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

public class ProviderOfferDataSource extends PageKeyedDataSource<Integer, Offer> {

    private static final int FIRST_PAGE = 0;
    private int providerId;
    private int pageSize;
    private String searchQuery;
    private List<String> categories;
    private List<String> eventTypes;
    private Boolean isAvailable, isFilter;
    private Double price;

    public ProviderOfferDataSource(int providerId, int pageSize, String searchQuery, List<String> categories, List<String> eventTypes, Boolean isAvailable, Double price, boolean filter) {
        this.providerId = providerId;
        this.pageSize = pageSize;
        this.searchQuery = searchQuery;
        this.categories = categories;
        this.eventTypes = eventTypes;
        this.isAvailable = isAvailable;
        this.price = price;
        this.isFilter = filter;
    }

    public void setFilter(boolean f) {
        this.isFilter = f;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Offer> callback) {
        fetchOffers(FIRST_PAGE, new Callback<PagedResponse<Offer>>() {
            @Override
            public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getContent(), null, FIRST_PAGE + 1);
                } else {
                    Log.e("ProviderOfferDataSource", "Response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                Log.e("ProviderOfferDataSource", "Error loading initial data: " + t.getMessage());
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Offer> callback) {
        // Not needed for forward pagination
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Offer> callback) {
        fetchOffers(params.key, new Callback<PagedResponse<Offer>>() {
            @Override
            public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getContent(), params.key + 1);
                } else {
                    Log.e("ProviderOfferDataSource", "Response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                Log.e("ProviderOfferDataSource", "Error loading next page: " + t.getMessage());
            }
        });
    }

    private void fetchOffers(int page, Callback<PagedResponse<Offer>> callback) {
        if (!isFilter) {
            Log.d("ProviderOfferDataSource", "Searching offers with page: " + page);
            ClientUtils.offerService.getSearchedService(providerId, searchQuery, page, pageSize).enqueue(callback);
        } else {
            Log.d("ProviderOfferDataSource", "Filtering offers with page: " + page);
            ClientUtils.offerService.getFilteredServices(providerId, categories, eventTypes, isAvailable, price, page, pageSize).enqueue(callback);
        }
    }

}
