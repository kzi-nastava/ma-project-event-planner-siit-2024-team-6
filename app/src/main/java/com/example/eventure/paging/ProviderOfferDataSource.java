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

public class ProviderOfferDataSource extends PageKeyedDataSource<Integer, Offer> {

    private static final int FIRST_PAGE = 0;
    private int providerId;
    private int pageSize;
    private String query;

    public ProviderOfferDataSource(int providerId, int pageSize, String query) {
        this.providerId = providerId;
        this.pageSize = pageSize;
        this.query = query;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Offer> callback) {
        Log.d("ProviderOfferDataSource", "loadInitial called with query: " + query);
        ClientUtils.offerService.getSearchedService(providerId, query, FIRST_PAGE, pageSize)
                .enqueue(new Callback<PagedResponse<Offer>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                        Log.d("ProviderOfferDataSource", "HTTP request successful");
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onResult(response.body().getContent(), null, FIRST_PAGE + 1);
                        } else {
                            Log.d("ProviderOfferDataSource", "HTTP response not successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                        Log.e("ProviderOfferDataSource", "HTTP request failed: " + t.getMessage());
                    }
                });
    }


    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Offer> callback) {
        // Not needed for forward pagination
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Offer> callback) {
        ClientUtils.offerService.getSearchedService(providerId, query, params.key, pageSize)
                .enqueue(new Callback<PagedResponse<Offer>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<Offer>> call, Response<PagedResponse<Offer>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onResult(response.body().getContent(), params.key + 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<Offer>> call, Throwable t) {
                        // Handle failure
                    }
                });
    }
}
