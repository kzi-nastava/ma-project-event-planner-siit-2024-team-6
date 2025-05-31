package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.adapters.OfferCarouselAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.OfferService;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

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
    private String currentName = null;
    private String currentDescription = null;
    private Double currentMaxPrice = null;
    private Boolean currentIsOnSale = null;
    private String currentStartDate = null;
    private String currentEndDate = null;
    private String currentCategory = null;
    private String currentEventType = null;
    private Boolean currentIsService = null;
    private Boolean currentIsProduct = null;

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
        offerAdapter = new OfferAdapter();
        offerRecyclerView.setAdapter(offerAdapter);
        // Fetch top 5
        fetchTopOffers(rootView);
        // Fetch the first page
        loadMoreButton = rootView.findViewById(R.id.loadMoreOffers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        offerRecyclerView.setLayoutManager(layoutManager);
        offerRecyclerView.setNestedScrollingEnabled(false);
        fetchOffersWithPagination(currentPage, loadMoreButton);

        // Setup Load More button click
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && offerAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                if (isFilterMode) {
                    fetchFilteredOffers(searchInput,searchInput,currentMaxPrice,currentIsOnSale,currentStartDate,currentEndDate,currentCategory,currentEventType,currentIsService,currentIsProduct,currentPage);
                } else {
                    fetchOffersWithPagination(currentPage, loadMoreButton);
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
                searchInput = query;
                resetFilter();
                offerAdapter.clearOffers();
                fetchFilteredOffers(searchInput,searchInput,currentMaxPrice,currentIsOnSale,currentStartDate,currentEndDate,currentCategory,currentEventType,currentIsService,currentIsProduct,currentPage);
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
                    fetchOffersWithPagination(currentPage, loadMoreButton);
                }
                return false;
            }
        });

        ImageView filterIcon = rootView.findViewById(R.id.offers_filter_icon);
        filterIcon.setOnClickListener(v -> showFilterDialog(inflater));

        return rootView;
    }

    private void fetchOffersWithPagination(int page, Button loadMoreButton) {
        if (isLoading) return;

        isLoading = true;  // Set loading state
        Log.d("OffersFragment", "Fetching page: " + page);

        offerService.getPagedOffers(page, 10).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                isLoading = false;  // Reset loading state

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<OfferDTO> pagedResponse = response.body();
                    totalItemsCount = pagedResponse.getTotalElements();
                    Log.d("OffersFragment", "Total  " + totalItemsCount);

                    List<OfferDTO> newOffers = pagedResponse.getContent();
                    Log.d("OffersFragment", "Fetched " + newOffers.size() + " offers from page: " + page);

                    Log.d("EventsTag", "DODAO OFFERS");

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

/*
    private void fetchAllOffers(View rootView, LayoutInflater inflater) {
        ScrollView parentScrollView = rootView.findViewById(R.id.parentScrollView);

        offerService.getAll().enqueue(new Callback<List<OfferDTO>>() {
            @Override
            public void onResponse(Call<List<OfferDTO>> call, Response<List<OfferDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OfferDTO> offers = response.body();

                    offerAdapter = new OfferAdapter(offers);
                    offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    offerRecyclerView.setAdapter(offerAdapter);

                    // Adjust RecyclerView height
                    parentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        boolean isHeightCalculated = false;

                        @Override
                        public void onScrollChanged() {
                            if (!isHeightCalculated) {
                                calculateAndSetRecyclerViewHeight(offerRecyclerView);
                                isHeightCalculated = true;
                            }
                        }
                    });
                } else {
                    Log.e("OffersFragment", "Failed to fetch all offers.");
                }
            }

            @Override
            public void onFailure(Call<List<OfferDTO>> call, Throwable t) {
                Log.e("OffersFragment", "Error fetching all offers", t);
            }
        });

    }

    // Calculate the height of all items in RecyclerView and set the height
    private void calculateAndSetRecyclerViewHeight(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View view = LayoutInflater.from(recyclerView.getContext())
                    .inflate(R.layout.product_card, recyclerView, false);

            view.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            totalHeight += view.getMeasuredHeight();
        }

        // Set the calculated height to the RecyclerView
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight;
        recyclerView.setLayoutParams(params);
    }
*/

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

        // Find the close icon in the filter layout
        ImageView closeIcon = dialogView.findViewById(R.id.close_icon);

        // Set an OnClickListener to dismiss the BottomSheetDialog when the close icon is clicked
        closeIcon.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

        // Show the dialog
        bottomSheetDialog.show();
    }

    private void fetchFilteredOffers(
            String name,
            String description,
            Double maxPrice,
            Boolean isOnSale,
            String startDate,
            String endDate,
            String category,
            String eventType,
            Boolean isService,
            Boolean isProduct,
            int page
    ) {
        isLoading = true;

        offerService.getFilteredOffers(
                name,
                description,
                maxPrice,
                isOnSale,
                startDate,
                endDate,
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
        currentName = null;
        currentDescription = null;
        currentMaxPrice = null;
        currentIsOnSale = null;
        currentStartDate = null;
        currentEndDate = null;
        currentCategory = null;
        currentEventType = null;
        currentIsService = null;
        currentIsProduct = null;
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