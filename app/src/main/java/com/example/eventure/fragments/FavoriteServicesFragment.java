package com.example.eventure.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteServicesFragment extends Fragment {

    private RecyclerView offerRecyclerView;
    private OfferAdapter offerAdapter;
    private View loadMoreButton;
    private View emptyOffers;

    private int currentPage = 0;
    private int totalItemsCount = 1;
    private boolean isLoading = false;

    public FavoriteServicesFragment() {
        // Required empty constructor
    }

    public static FavoriteServicesFragment newInstance() {
        return new FavoriteServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_offers, container, false);

        offerRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        emptyOffers = rootView.findViewById(R.id.emptyOffers);
        loadMoreButton = rootView.findViewById(R.id.loadMoreOffers);

        offerAdapter = new OfferAdapter(getChildFragmentManager());
        offerRecyclerView.setAdapter(offerAdapter);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offerRecyclerView.setNestedScrollingEnabled(false);

        fetchFavoriteServices(currentPage);

        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && offerAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                fetchFavoriteServices(currentPage);
            }
        });

        return rootView;
    }

    private void fetchFavoriteServices(int page) {
        isLoading = true;
        ClientUtils.userService.getFavoriteServices(page, ClientUtils.PAGE_SIZE).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<OfferDTO> offers = response.body().getContent();
                    totalItemsCount = response.body().getTotalElements();

                    offerAdapter.addOffers(offers);
                    offerAdapter.notifyItemRangeInserted(
                            offerAdapter.getItemCount() - offers.size(),
                            offers.size()
                    );

                    updateLoadMoreVisibility();
                    toggleEmptyOffers();
                } else {
                    Log.e("FavoriteServices", "Failed response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<OfferDTO>> call, Throwable t) {
                isLoading = false;
                Log.e("FavoriteServices", "Error: ", t);
            }
        });
    }

    private void updateLoadMoreVisibility() {
        if (offerAdapter.getItemCount() < totalItemsCount) {
            loadMoreButton.setVisibility(View.VISIBLE);
        } else {
            loadMoreButton.setVisibility(View.GONE);
        }
    }

    private void toggleEmptyOffers() {
        if (offerAdapter.getItemCount() == 0) {
            emptyOffers.setVisibility(View.VISIBLE);
        } else {
            emptyOffers.setVisibility(View.GONE);
        }
    }
}
