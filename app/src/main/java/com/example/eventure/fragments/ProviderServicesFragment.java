package com.example.eventure.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ProviderOfferAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditServiceDialog;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;
import com.example.eventure.viewmodel.ProviderOfferViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProviderOfferAdapter offerAdapter;
    private ProgressBar progressBar;
    private ProviderOfferViewModel offerViewModel;
    private LinearLayout eventTypeContainer;
    private LinearLayout categoryContainer;

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
        }, offer -> {
            // Handle delete button click
            showDeleteConfirmationDialog(offer);
        });

        recyclerView.setAdapter(offerAdapter);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

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
        setupFilter(filterIcon, inflater);

        return rootView;
    }

    private void setupFilter(ImageView filterIcon, LayoutInflater inflater) {
        filterIcon.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View dialogView = inflater.inflate(R.layout.filter_provider_services, null);
            bottomSheetDialog.setContentView(dialogView);

            View bottomSheet = (View) dialogView.getParent();
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setDraggable(false);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // Close Icon Listener
            ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
            closeIcon.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

            // Containers for dynamically loaded checkboxes
            eventTypeContainer = dialogView.findViewById(R.id.event_type_container);
            categoryContainer = dialogView.findViewById(R.id.category_container);

            // Load categories and event types
            loadCategories();
            loadEventTypes();

            // Find the filter button
            Button filterButton = dialogView.findViewById(R.id.filter_button);

            // Add action listener for the filter button
            filterButton.setOnClickListener(v2 -> {
                // Collect selected categories
                List<String> selectedCategories = new ArrayList<>();
                for (int i = 0; i < categoryContainer.getChildCount(); i++) {
                    View view = categoryContainer.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (checkBox.isChecked()) {
                            selectedCategories.add(checkBox.getText().toString());
                        }
                    }
                }

                // Collect selected event types
                List<String> selectedEventTypes = new ArrayList<>();
                for (int i = 0; i < eventTypeContainer.getChildCount(); i++) {
                    View view = eventTypeContainer.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (checkBox.isChecked()) {
                            selectedEventTypes.add(checkBox.getText().toString());
                        }
                    }
                }

                // Get availability checkbox value
                CheckBox availabilityCheckBox = dialogView.findViewById(R.id.availability_checkbox);
                Boolean isAvailable = availabilityCheckBox.isChecked();

                // Get price range values from the RangeSlider
                RangeSlider priceRangeSlider = dialogView.findViewById(R.id.price_range_slider);
                Float maxPrice = priceRangeSlider.getValues().get(0);
                // Call the filtering method with the collected data
                filterOffers(selectedCategories, selectedEventTypes, isAvailable, (double) maxPrice);

                // Dismiss the dialog after applying filters
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });
    }


    // Method to show confirmation dialog
    private void showDeleteConfirmationDialog(Offer offer) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("Yes", (dialog, which) -> deleteOffer(offer))
                .setNegativeButton("No", null)
                .show();
    }

    // Method to call the delete API
    private void deleteOffer(Offer offer) {
        ClientUtils.offerService.deleteProviderService(offer.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    offerViewModel.refresh(); // Refresh the list after deletion
                } else {
                    Log.e("DELETE_OFFER", "FAILED");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void searchServices(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        offerViewModel.searchOffers(query);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void populateCheckboxes(LinearLayout container, List<String> items) {
        container.removeAllViews(); // Clear any existing views

        for (String item : items) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(item);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            // Add the checkbox to the container
            container.addView(checkBox);
        }
    }

    private void loadCategories() {
        ClientUtils.categoryService.getAllCategoryNames().enqueue(new Callback<List<String>>() {
            List<String> categoryList;
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    populateCheckboxes(categoryContainer, categoryList);
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FetchCategories", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadEventTypes() {
        ClientUtils.eventTypeService.findAllNames().enqueue(new Callback<List<String>>() {
            List<String> categoryList;
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    populateCheckboxes(eventTypeContainer, categoryList);
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FetchCategories", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterOffers(List<String> categories, List<String> eventTypes, Boolean isAvailable, Double price) {
        // Call the API or ViewModel method to fetch the filtered offers
        offerViewModel.filterOffers(categories, eventTypes, isAvailable, price);
    }

}
