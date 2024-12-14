package com.example.eventure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ProviderOfferAdapter;
import com.example.eventure.dialogs.EditServiceDialog;
import com.example.eventure.model.Offer;
import com.example.eventure.viewmodel.ProviderOfferViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProviderServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProviderOfferAdapter offerAdapter;
    private ProgressBar progressBar;
    private ProviderOfferViewModel offerViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_proivder_services, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.services_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = rootView.findViewById(R.id.progress_bar);

        // Initialize the offer adapter
        offerAdapter = new ProviderOfferAdapter(offer -> {
            // Handle edit button click
            EditServiceDialog dialog = EditServiceDialog.newInstance(offer);
            dialog.setOnOfferUpdatedListener(() -> offerViewModel.refresh());
            dialog.show(getChildFragmentManager(), "EditServiceDialog");
        });

        recyclerView.setAdapter(offerAdapter);

        // Initialize the ViewModel with providerId and pageSize
        offerViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProviderOfferViewModel(1, 10); // Example providerId = 1, pageSize = 10
            }
        }).get(ProviderOfferViewModel.class);

        // Observe the paged list and update the adapter
        offerViewModel.getPagedOffers().observe(getViewLifecycleOwner(), pagedList -> {
            offerAdapter.submitList(pagedList);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);
        setupFilterIcon(filterIcon, inflater);

        return rootView;
    }

    private void setupFilterIcon(ImageView filterIcon, LayoutInflater inflater) {
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
    }
}
