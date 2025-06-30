package com.example.eventure.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.UserService;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private TextView emptyText;
    private Button loadMoreButton;
    private UserService userService;
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalItemsCount = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_events, container, false);

        recyclerView = view.findViewById(R.id.eventRecyclerView);
        emptyText = view.findViewById(R.id.emptyEvents);
        loadMoreButton = view.findViewById(R.id.loadMoreEvents);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter();
        recyclerView.setAdapter(adapter);
        userService = ClientUtils.userService;

        loadFavorites(currentPage);

        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && adapter.getItemCount() < totalItemsCount) {
                currentPage++;
                loadFavorites(currentPage);
            }
        });

        return view;
    }

    private void loadFavorites(int page) {
        isLoading = true;

        userService.getPagedFavorites(page, ClientUtils.PAGE_SIZE)
                .enqueue(new Callback<PagedResponse<EventDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<EventDTO>> call, Response<PagedResponse<EventDTO>> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            PagedResponse<EventDTO> result = response.body();
                            totalItemsCount = result.getTotalElements();

                            List<EventDTO> favorites = result.getContent();
                            adapter.addEvents(favorites);
                            adapter.notifyItemRangeInserted(adapter.getItemCount() - favorites.size(), favorites.size());

                            toggleEmptyText();
                            toggleLoadMoreVisibility();
                        } else {
                            Log.e("FavoritesFragment", "Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<EventDTO>> call, Throwable t) {
                        isLoading = false;
                        Log.e("FavoritesFragment", "Failed to fetch favorites", t);
                    }
                });
    }

    private void toggleEmptyText() {
        if (adapter.getItemCount() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    private void toggleLoadMoreVisibility() {
        if (adapter.getItemCount() < totalItemsCount) {
            loadMoreButton.setVisibility(View.VISIBLE);
        } else {
            loadMoreButton.setVisibility(View.GONE);
        }
    }
}
