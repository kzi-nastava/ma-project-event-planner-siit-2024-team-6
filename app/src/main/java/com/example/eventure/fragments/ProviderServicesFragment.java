package com.example.eventure.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ProviderOfferAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditServiceDialog;
import com.example.eventure.model.Offer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProviderOfferAdapter offerAdapter;
    private List<Offer> offerList;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_proivder_services, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.services_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = rootView.findViewById(R.id.progress_bar);

        // Initialize the offer list and adapter with an empty list
        offerList = new ArrayList<>();
        offerAdapter = new ProviderOfferAdapter(offerList, offer -> {

            // Handle edit button click
            EditServiceDialog dialog = EditServiceDialog.newInstance(offer);
            dialog.setOnOfferUpdatedListener(() -> {
                // Refresh the data in the fragment
                fetchOffers();
            });
            dialog.show(getChildFragmentManager(), "EditServiceDialog");
        });

        recyclerView.setAdapter(offerAdapter);

        // Fetch the data
        fetchOffers();

        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);

        // Filter Icon Click Listener
        filterIcon.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View dialogView = inflater.inflate(R.layout.filter_provider_services, null);
            bottomSheetDialog.setContentView(dialogView);

            View bottomSheet = (View) dialogView.getParent();
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setDraggable(false);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
            closeIcon.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

            bottomSheetDialog.show();
        });

        return rootView;
    }

    private void fetchOffers() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        Call<List<Offer>> call = ClientUtils.offerService.getProviderServices(1);

        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    offerList.clear(); // Clear the existing data
                    offerList.addAll(response.body()); // Add the new data

                    // Check if offerAdapter is not null before calling notifyDataSetChanged
                    if (offerAdapter != null) {
                        offerAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("AdapterError", "OfferAdapter is null");
                    }
                } else {
                    Log.e("API_ERROR", "Response Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to fetch offers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Offer>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Log.e("Error", t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
