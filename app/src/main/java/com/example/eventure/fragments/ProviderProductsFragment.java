package com.example.eventure.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ProviderOfferAdapter;
import com.example.eventure.adapters.ProviderProductAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditProductDialog;
import com.example.eventure.model.Offer;
import com.example.eventure.viewmodel.ProviderOfferViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProviderProductAdapter productAdapter;
    private ProgressBar progressBar;
    private ProviderOfferViewModel offerViewModel;
    private Spinner eventTypeSpinner;
    private Spinner categorySpinner;
    private TextView emptyView;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_provider_products, container, false);
        root = rootView;

        recyclerView = rootView.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyView = rootView.findViewById(R.id.empty_view);
        progressBar = rootView.findViewById(R.id.progress_bar);

        productAdapter = new ProviderProductAdapter(offer -> {
            EditProductDialog dialog = EditProductDialog.newInstance(offer);
            dialog.setOnOfferUpdatedListener(() -> offerViewModel.refresh());
            dialog.show(getChildFragmentManager(), "EditProductDialog");
        }, offer -> showDeleteConfirmationDialog(offer));

        recyclerView.setAdapter(productAdapter);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        offerViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProviderOfferViewModel(10);
            }
        }).get(ProviderOfferViewModel.class);

        offerViewModel.getPagedOffers().observe(getViewLifecycleOwner(), pagedList -> {
            productAdapter.submitList(pagedList);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);
        setupFilter(filterIcon, inflater);

        return rootView;
    }

    private void setupFilter(ImageView filterIcon, LayoutInflater inflater) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = inflater.inflate(R.layout.filter_provider_services, null); // можно сделать отдельный layout под продукты при желании

        CheckBox availabilityCheckBox = dialogView.findViewById(R.id.availability_checkbox);
        availabilityCheckBox.setChecked(true);

        eventTypeSpinner = dialogView.findViewById(R.id.event_type_spinner);
        categorySpinner = dialogView.findViewById(R.id.category_spinner);

        loadCategoriesIntoSpinner();
        loadEventTypesIntoSpinner();

        RangeSlider priceRangeSlider = dialogView.findViewById(R.id.price_range_slider);
        priceRangeSlider.setValues(priceRangeSlider.getValueTo());

        filterIcon.setOnClickListener(v -> {
            bottomSheetDialog.setContentView(dialogView);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
            bottomSheetBehavior.setDraggable(false);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            dialogView.findViewById(R.id.close_icon).setOnClickListener(v1 -> bottomSheetDialog.dismiss());

            dialogView.findViewById(R.id.filter_button).setOnClickListener(v2 -> {
                String category = categorySpinner.getSelectedItem().toString();
                String eventType = eventTypeSpinner.getSelectedItem().toString();

                if ("Select Category".equals(category)) category = null;
                if ("Select Event Type".equals(eventType)) eventType = null;

                Boolean isAvailable = availabilityCheckBox.isChecked();
                Boolean onSale = ((CheckBox) dialogView.findViewById(R.id.sale_checkbox)).isChecked();
                Double maxPrice = Double.valueOf(priceRangeSlider.getValues().get(0));

                filterOffers(category, eventType, onSale, isAvailable, maxPrice);
                bottomSheetDialog.dismiss();
            });

            dialogView.findViewById(R.id.reset_button).setOnClickListener(v3 -> {
                categorySpinner.setSelection(0);
                eventTypeSpinner.setSelection(0);
                availabilityCheckBox.setChecked(true);
                ((CheckBox) dialogView.findViewById(R.id.sale_checkbox)).setChecked(false);
                priceRangeSlider.setValues(priceRangeSlider.getValueTo());
                filterOffers(null, null, false, true, (double) priceRangeSlider.getValueTo());
                bottomSheetDialog.dismiss();
                Toast.makeText(getContext(), "Filters reset", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        });
    }

    private void showDeleteConfirmationDialog(Offer offer) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteOffer(offer))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteOffer(Offer offer) {
        ClientUtils.offerService.deleteProviderProduct(offer.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    offerViewModel.refresh();
                    showSnackbar("Product deleted successfully");
                } else {
                    showSnackbar("Error: failed to delete product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE_PRODUCT", "Failure: " + t.getMessage());
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(this.root, message, Snackbar.LENGTH_LONG).show();
    }

    public void searchProducts(String query) {
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
                    categoryList.add("Select Category");
                    categoryList.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventTypesIntoSpinner() {
        ClientUtils.eventTypeService.findAllNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> eventTypes = new ArrayList<>();
                    eventTypes.add("Select Event Type");
                    eventTypes.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, eventTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventTypeSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOffers(String category, String eventType, Boolean onSale, Boolean isAvailable, Double price) {
        offerViewModel.filterOffers(category, eventType, onSale, isAvailable, price);
    }
}
