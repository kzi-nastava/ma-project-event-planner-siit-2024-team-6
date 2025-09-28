package com.example.eventure.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.eventure.clients.ClientUtils;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderProductDataSource extends PageKeyedDataSource<Integer, Offer> {

    private static final int FIRST_PAGE = 0;
    private final int pageSize;
    private final String type = "product";
    private String query, category, eventType;
    private Boolean onSale, isAvailable, isFilter;
    private Double price;

    public ProviderProductDataSource(int pageSize, String query, String category, String eventType,
                                     Boolean onSale, Boolean isAvailable, Double price, boolean isFilter) {
        this.pageSize = pageSize;
        this.query = query;
        this.category = category;
        this.eventType = eventType;
        this.onSale = onSale;
        this.isAvailable = isAvailable;
        this.price = price;
        this.isFilter = isFilter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Offer> callback) {
        fetchProducts(FIRST_PAGE, new Callback<>() {
            @Override
            public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getContent(), null, FIRST_PAGE + 1);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                Log.e("ProviderProductDataSource", "Initial load failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Offer> callback) {
        fetchProducts(params.key, new Callback<>() {
            @Override
            public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getContent(), params.key + 1);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                Log.e("ProviderProductDataSource", "Next page load failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Offer> callback) {
        // not used
    }

    private void fetchProducts(int page, Callback<PagedResponse<Offer>> callback) {
        ClientUtils.offerService.getMyProducts(page, pageSize).enqueue(callback);
    }
}
