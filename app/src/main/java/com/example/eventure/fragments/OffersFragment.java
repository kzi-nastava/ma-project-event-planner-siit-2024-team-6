package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.adapters.OfferCarouselAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.OfferService;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {

    private OfferService offerService;
    // ui
    private ViewPager2 offerCarousel;
    private OfferCarouselAdapter carouselAdapter;
    private RecyclerView offerRecyclerView;
    private OfferAdapter offerAdapter;
    private TextView emptyOffers;
    private Button loadMoreButton;

    // pagination params
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalItemsCount = 1;

    //filter
    private boolean isFilterMode = false;
    private Double currentMinPrice = Double.valueOf(0);
    private Double currentMaxPrice = Double.valueOf(0);
    private Boolean currentIsOnSale = false;
    private String currentCategory = null;
    private String currentEventType = null;
    private Boolean currentIsService = true;
    private Boolean currentIsProduct = true;
    private List<String> eventTypes = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    //Search
    private String searchInput;
    public OffersFragment() {
        // Required empty public constructor
    }


    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("MethodsTag", "OffersFragment onCreate called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MethodsTag", "OffersFragment onCreateView called");

        View rootView = inflater.inflate(R.layout.fragment_offer, container, false);

        offerService = ClientUtils.offerService;

        offerCarousel = rootView.findViewById(R.id.offerCarousel);
        offerRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        emptyOffers = rootView.findViewById(R.id.emptyOffers);
        offerAdapter = new OfferAdapter(getChildFragmentManager());
        offerRecyclerView.setAdapter(offerAdapter);
        // Fetch top 5
        fetchTopOffers(rootView);
        // Fetch the first page
        loadMoreButton = rootView.findViewById(R.id.loadMoreOffers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        offerRecyclerView.setLayoutManager(layoutManager);
        offerRecyclerView.setNestedScrollingEnabled(false);
        fetchOffersWithPagination(currentPage);

        // Setup Load More button click
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && offerAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                if (isFilterMode) {
                    fetchFilteredOffers(searchInput,searchInput,currentMinPrice,currentMaxPrice,currentIsOnSale,currentCategory,currentEventType,currentIsService,currentIsProduct,currentPage);
                } else {
                    fetchOffersWithPagination(currentPage);
                }
            }
        });
        // Set listener for search
        SearchView searchView = rootView.findViewById(R.id.offers_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("OffersTag",query);
                isFilterMode = true;
                currentPage = 0;
                resetFilter();
                searchInput = query;
                offerAdapter.clearOffers();
                fetchFilteredOffers(searchInput,searchInput,currentMinPrice,currentMaxPrice,currentIsOnSale,currentCategory,currentEventType,currentIsService,currentIsProduct,currentPage);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // When input is cleared, reload all offers
                if (newText == null || newText.trim().isEmpty()) {
                    isFilterMode = false;
                    searchInput = null;
                    currentPage = 0;
                    offerAdapter.clearOffers();
                    fetchOffersWithPagination(currentPage);
                }
                return false;
            }
        });

        // Fetching event types for filter dropdown
        fetchEventTypes(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                eventTypes.clear();
                eventTypes.addAll(response.body());
                eventTypes.add(0, "Select Event Type");
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
            }
        });

        // Fetching categories for filter dropdown
        fetchCategories(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                categories.clear();
                categories.addAll(response.body());
                categories.add(0, "Select Category");
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("OffersTag", "Fetching categories failed: " + t.getMessage(), t);
            }
        });

        ImageView filterIcon = rootView.findViewById(R.id.offers_filter_icon);
        filterIcon.setOnClickListener(v -> showFilterDialog(inflater));

        return rootView;
    }

    private void fetchOffersWithPagination(int page) {
        if (isLoading) return;
        Log.d("OffersTag", "FETCH ALL");

        isLoading = true;  // Set loading state
        Log.d("OffersTag", "Fetching page: " + page);

        offerService.getPagedOffers(page, 10).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                isLoading = false;  // Reset loading state

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<OfferDTO> pagedResponse = response.body();
                    totalItemsCount = pagedResponse.getTotalElements();
                    Log.d("OffersTag", "Total  " + totalItemsCount);

                    List<OfferDTO> newOffers = pagedResponse.getContent();
                    Log.d("OffersTag", "Fetched " + newOffers.size() + " offers from page: " + page);

                    Log.d("OffersTag", "DODAO OFFERS");

                    offerAdapter.addOffers(newOffers);  // Append new offers to the list
                    offerAdapter.notifyItemRangeInserted(
                            offerAdapter.getItemCount() - newOffers.size(),
                            newOffers.size()
                    );

                    // Show or hide the Load More button based on remaining pages
                    if (offerAdapter.getItemCount() < totalItemsCount) {
                        loadMoreButton.setVisibility(View.VISIBLE);
                    } else {
                        loadMoreButton.setVisibility(View.GONE);

                        // Adjust RecyclerView bottom margin when button is hidden
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) offerRecyclerView.getLayoutParams();
                        params.bottomMargin = (int) (50 * getResources().getDisplayMetrics().density);  // 70dp in pixels
                        offerRecyclerView.setLayoutParams(params);
                    }
                } else {
                    Log.e("OffersFragment", "Failed to fetch offers. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<OfferDTO>> call, Throwable t) {
                isLoading = false;  // Reset loading state
                Log.e("OffersFragment", "Error fetching offers", t);
            }
        });
    }
    private void fetchTopOffers(View rootView) {
        ImageButton offerPrevButton = rootView.findViewById(R.id.offerPrevButton);
        ImageButton offerNextButton = rootView.findViewById(R.id.offerNextButton);

        offerService.getTopFive().enqueue(new Callback<List<OfferDTO>>() {
            @Override
            public void onResponse(Call<List<OfferDTO>> call, Response<List<OfferDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OfferDTO> offers = response.body();

                    carouselAdapter = new OfferCarouselAdapter(offers);
                    offerCarousel.setAdapter(carouselAdapter);

                    // Apply carousel transformations
                    offerCarousel.setOffscreenPageLimit(3);
                    CompositePageTransformer transformer = new CompositePageTransformer();
                    transformer.addTransformer(new MarginPageTransformer(40));
                    transformer.addTransformer((page, position) -> {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    });
                    offerCarousel.setPageTransformer(transformer);

                    // Arrow Button Functionality
                    offerPrevButton.setOnClickListener(v -> {
                        int currentItem = offerCarousel.getCurrentItem();
                        if (currentItem > 0) {
                            offerCarousel.setCurrentItem(currentItem - 1, true);
                        }
                    });

                    offerNextButton.setOnClickListener(v -> {
                        int currentItem = offerCarousel.getCurrentItem();
                        if (currentItem < carouselAdapter.getItemCount() - 1) {
                            offerCarousel.setCurrentItem(currentItem + 1, true);
                        }
                    });
                } else {
                    Log.e("OffersFragment", "Failed to fetch top offers.");
                }
            }

            @Override
            public void onFailure(Call<List<OfferDTO>> call, Throwable t) {
                Log.e("OffersFragment", "Error fetching top offers", t);
            }
        });
    }

    private void showFilterDialog(LayoutInflater inflater) {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        // Inflate the filter layout
        View dialogView = inflater.inflate(R.layout.filter_offers, null);
        bottomSheetDialog.setContentView(dialogView);



        // Access the BottomSheetBehavior from the BottomSheetDialog
        View bottomSheet = (View) dialogView.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        // Disable dragging to dismiss
        bottomSheetBehavior.setDraggable(false);
        // Optionally, set the initial state to expanded (or collapsed) if needed
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);



        ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        Spinner eventTypeSpinner = dialogView.findViewById(R.id.spinner_event_type);
        CheckBox onSaleCheckBox = dialogView.findViewById(R.id.checkbox_on_sale);
        CheckBox productCheckBox = dialogView.findViewById(R.id.checkbox_product);
        CheckBox serviceCheckBox = dialogView.findViewById(R.id.checkbox_service);
        resetFilter();
        productCheckBox.setChecked(true);
        serviceCheckBox.setChecked(true);

        RangeSlider priceSlider = dialogView.findViewById(R.id.price_range_slider);
        priceSlider.setValues(0f, 0f);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, eventTypes);
        eventTypeSpinner.setAdapter(eventTypeAdapter);


        // RESET button
        Button resetButton = dialogView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            try {
                resetFilter();
                categorySpinner.setSelection(0);
                eventTypeSpinner.setSelection(0);
                productCheckBox.setChecked(true);
                serviceCheckBox.setChecked(true);
                onSaleCheckBox.setChecked(false);
                priceSlider.setValues(0f, 0f);
                //search without filter when filter is reset
                currentPage = 0;
                offerAdapter.clearOffers();
                fetchOffersWithPagination(currentPage);
            } catch (Exception e) {
                Log.e("OffersTag", "Reset error: " + e.getMessage(), e);
            }
        });

        Button applyButton = dialogView.findViewById(R.id.filter_button);
        applyButton.setOnClickListener(v -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String selectedEventType = eventTypeSpinner.getSelectedItem().toString();
            currentCategory = selectedCategory.equals("Select Category") ? null : selectedCategory;
            currentEventType = selectedEventType.equals("Select Event Type") ? null : selectedEventType;
            currentIsOnSale = onSaleCheckBox.isChecked() ? true : false;
            currentIsProduct = productCheckBox.isChecked() ? true : false;
            currentIsService = serviceCheckBox.isChecked() ? true : false;
            List<Float> values = priceSlider.getValues();
            currentMinPrice = Double.valueOf(values.get(0));
            currentMaxPrice = Double.valueOf(values.get(1));
            if (isOffersFilterEmpty(
                    currentCategory,
                    currentEventType,
                    currentMinPrice,
                    currentMaxPrice,
                    currentIsOnSale,
                    currentIsProduct,
                    currentIsService
            )) {
                bottomSheetDialog.dismiss();
                return;
            }
            bottomSheetDialog.dismiss();
            isFilterMode = true;
            currentPage = 0;
            offerAdapter.clearOffers();
            fetchFilteredOffers(null, null, currentMinPrice, currentMaxPrice, currentIsOnSale,
                    currentCategory, currentEventType, currentIsService, currentIsProduct, currentPage);
        });
        // Show the dialog
        bottomSheetDialog.show();
    }

    private void fetchFilteredOffers(
            String name,
            String description,
            Double minPrice,
            Double maxPrice,
            Boolean isOnSale,
            String category,
            String eventType,
            Boolean isService,
            Boolean isProduct,
            int page
    ) {
        isLoading = true;
        Log.d("OffersTag", "FETCH FILTER");

        Log.d("OffersTag", "Fetching filtered offers with parameters:");
        Log.d("OffersTag", "name: " + name);
        Log.d("OffersTag", "description: " + description);
        Double minPriceParam = (minPrice != null && minPrice == 0.0) ? null : minPrice;
        Double maxPriceParam = (maxPrice != null && maxPrice == 0.0) ? null : maxPrice;
        Log.d("OffersTag", "minPrice: " + minPriceParam);
        Log.d("OffersTag", "maxPrice: " + maxPriceParam);
        Log.d("OffersTag", "isOnSale: " + isOnSale);
        Log.d("OffersTag", "category: " + category);
        Log.d("OffersTag", "eventType: " + eventType);
        Log.d("OffersTag", "isService: " + isService);
        Log.d("OffersTag", "isProduct: " + isProduct);
        offerService.getFilteredOffers(
                name,
                description,
                minPriceParam,
                maxPriceParam,
                isOnSale,
                category,
                eventType,
                isService,
                isProduct,
                page,
                ClientUtils.PAGE_SIZE
        ).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<OfferDTO> pagedResponse = response.body();

                    totalItemsCount = pagedResponse.getTotalElements();
                    Log.d("OffersTag", "Fetched TOTAL " + totalItemsCount + " filtered offers on page: " + page);

                    List<OfferDTO> filteredOffers = pagedResponse.getContent();

                    if (filteredOffers != null && !filteredOffers.isEmpty()) {
                        Log.d("OffersTag", "Fetched " + filteredOffers.size() + " filtered offers on page: " + page);
                        offerAdapter.addOffers(filteredOffers);
                        offerAdapter.notifyItemRangeInserted(
                                offerAdapter.getItemCount() - filteredOffers.size(),
                                filteredOffers.size()
                        );
                    } else {
                        Log.d("OffersTag", "No filtered offers on page: " + page);
                    }
                } else {
                    totalItemsCount = 0;
                    Log.d("OffersTag", "No filtered offers fetched: " + response.code());
                }

                toggleEmptyOffers();
                updateLoadMoreVisibility();
            }

            @Override
            public void onFailure(Call<PagedResponse<OfferDTO>> call, Throwable t) {
                isLoading = false;
                Log.e("OffersTag", "Error loading filtered offers: " + t.getMessage());
            }
        });
    }
    public void fetchEventTypes(Callback<List<String>> callback) {
        ClientUtils.eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventTypeDTO> eventTypes = response.body();
                    List<String> eventTypeNames = new ArrayList<>();
                    for (EventTypeDTO eventType : eventTypes) {
                        eventTypeNames.add(eventType.getName());
                    }
                    callback.onResponse(null, Response.success(eventTypeNames));
                } else {
                    callback.onFailure(null, new Throwable("Response for event types unsuccessful or empty"));
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }
    public void fetchCategories(Callback<List<String>> callback) {
        ClientUtils.categoryService.getCategories().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categoriesNames = response.body();
                    callback.onResponse(null, Response.success(categoriesNames));
                } else {
                    callback.onFailure(null, new Throwable("Response for categories unsuccessful or empty"));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Log network or Retrofit-level failures
                Log.e("OffersTag", "fetchCategories() failed: " + t.getMessage(), t);
                callback.onFailure(null, t);
            }
        });
    }


    private void updateLoadMoreVisibility() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) offerRecyclerView.getLayoutParams();
        if (offerAdapter.getItemCount() < totalItemsCount) {
            loadMoreButton.setVisibility(View.VISIBLE);
            params.bottomMargin = 0;
        } else {
            loadMoreButton.setVisibility(View.GONE);
            params.bottomMargin = (int) (70 * getResources().getDisplayMetrics().density);
        }
        offerRecyclerView.setLayoutParams(params);
    }
    private void toggleEmptyOffers() {
        if (offerAdapter.getItemCount() == 0) {
            emptyOffers.setVisibility(View.VISIBLE);
        } else {
            emptyOffers.setVisibility(View.GONE);
        }
    }
    private void resetFilter() {
        searchInput = null;
        currentMinPrice = Double.valueOf(0);
        currentMaxPrice = Double.valueOf(0);
        currentIsOnSale = false;
        currentCategory = null;
        currentEventType = null;
        currentIsService = true;
        currentIsProduct = true;

    }
    private boolean isOffersFilterEmpty(String selectedCategory, String selectedEventType,
                                        Double minPrice, Double maxPrice,
                                        boolean isOnSaleChecked,
                                        boolean isProduct,
                                        boolean isService) {
        boolean noCategorySelected = selectedCategory == null || "Select Category".equals(selectedCategory);
        boolean noEventTypeSelected = selectedEventType == null || "Select Event Type".equals(selectedEventType);
        boolean noPriceFilter = (minPrice == null || minPrice == 0f) && (maxPrice == null || maxPrice == 0f);
        boolean noOnSaleChecked = !isOnSaleChecked;
        // if both are true (default) then user did not choose a new option while filtering
        boolean typeNotChanged = isProduct == true && isService == true;

        return noCategorySelected && noEventTypeSelected && noPriceFilter
                && noOnSaleChecked && typeNotChanged;
    }

    private String formatDate(Long millis) {
        if (millis == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
    @Override
    public void onStart() {
        Log.e("MethodsTag", "OffersFragment onStart called");

        super.onStart();
        // Logic to execute when the fragment becomes visible
    }

    @Override
    public void onResume() {
        Log.e("MethodsTag", "OffersFragment onResume called");

        super.onResume();
        // Logic to execute when the fragment is interactable
    }

    @Override
    public void onPause() {
        Log.e("MethodsTag", "OffersFragment onPause called");

        super.onPause();
        // Save changes or pause actions
    }

    @Override
    public void onStop() {
        Log.e("MethodsTag", "OffersFragment onStop called");

        super.onStop();
        // Logic to execute when the fragment is no longer visible
    }

    @Override
    public void onDestroyView() {
        Log.e("MethodsTag", "OffersFragment onDestoryView called");

        super.onDestroyView();
        // Clean up resources related to the view
    }

    @Override
    public void onDestroy() {
        Log.e("MethodsTag", "OffersFragment onDestroy called");

        super.onDestroy();
        // Final cleanup logic
    }
}