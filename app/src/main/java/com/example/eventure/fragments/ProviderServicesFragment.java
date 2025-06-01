package com.example.eventure.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner eventTypeSpinner;
    private Spinner categorySpinner;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_proivder_services, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.services_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyView = rootView.findViewById(R.id.empty_view);

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
                return (T) new ProviderOfferViewModel(10); //pageSize = 10
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = inflater.inflate(R.layout.filter_provider_services, null);

        CheckBox availabilityCheckBox = dialogView.findViewById(R.id.availability_checkbox);
        availabilityCheckBox.setChecked(true);

        // Containers for dynamically loaded checkboxes
        eventTypeSpinner = dialogView.findViewById(R.id.event_type_spinner);
        categorySpinner = dialogView.findViewById(R.id.category_spinner);

        // Load categories and event types
        loadCategoriesIntoSpinner();
        loadEventTypesIntoSpinner();

        RangeSlider priceRangeSlider = dialogView.findViewById(R.id.price_range_slider);
        priceRangeSlider.setValues(priceRangeSlider.getValueTo());

        filterIcon.setOnClickListener(v -> {

            bottomSheetDialog.setContentView(dialogView);

            View bottomSheet = (View) dialogView.getParent();
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setDraggable(false);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // Close Icon Listener
            ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
            closeIcon.setOnClickListener(v1 -> bottomSheetDialog.dismiss());


            // Find the filter button
            Button filterButton = dialogView.findViewById(R.id.filter_button);

            // Add action listener for the filter button
            filterButton.setOnClickListener(v2 -> {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String selectedEventType = eventTypeSpinner.getSelectedItem().toString();

                if (selectedCategory.equals("Select Category")) {
                    selectedCategory = null;
                }

                if (selectedEventType.equals("Select Event Type")) {
                    selectedEventType = null;
                }

                // Get availability checkbox value
                Boolean isAvailable = availabilityCheckBox.isChecked();

                CheckBox saleCheckBox = dialogView.findViewById(R.id.sale_checkbox);
                Boolean onSale = saleCheckBox.isChecked();

                // Get price range values from the RangeSlider
                Float maxPrice = priceRangeSlider.getValues().get(0);

                // Call the filtering method with the collected dat0
                filterOffers(selectedCategory, selectedEventType, onSale, isAvailable, (double) maxPrice);

                // Dismiss the dialog after applying filters
                bottomSheetDialog.dismiss();
            });

            Button resetButton = dialogView.findViewById(R.id.reset_button);
            resetButton.setOnClickListener(v3 -> {
                categorySpinner.setSelection(0); // back to "Select Category"
                eventTypeSpinner.setSelection(0); // back to "Select Event Type"

                availabilityCheckBox.setChecked(true);

                CheckBox saleCheckBox = dialogView.findViewById(R.id.sale_checkbox);
                saleCheckBox.setChecked(false);

                priceRangeSlider.setValues(priceRangeSlider.getValueTo());

                // Call the filtering method with the collected dat0
                filterOffers(null, null, false, true, (double) priceRangeSlider.getValueTo());

                // Dismiss the dialog after applying filters
                bottomSheetDialog.dismiss();

                Toast.makeText(getContext(), "Filters reset", Toast.LENGTH_SHORT).show();
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

    private void loadCategoriesIntoSpinner() {
        ClientUtils.categoryService.getAllCategoryNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categoryList = new ArrayList<>();
                    categoryList.add("Select Category"); // default
                    categoryList.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventTypesIntoSpinner() {
        ClientUtils.eventTypeService.findAllNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> eventTypes = new ArrayList<>();
                    eventTypes.add("Select Event Type"); // default
                    eventTypes.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, eventTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventTypeSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOffers(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        // Call the API or ViewModel method to fetch the filtered offers
        offerViewModel.filterOffers(category, eventType, onSale, isAvailable, price);
    }

}
