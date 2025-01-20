package com.example.eventure.fragments;

import android.os.Bundle;

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

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.adapters.OfferCarouselAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.OfferService;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OfferService offerService;
    private ViewPager2 offerCarousel;
    private OfferCarouselAdapter carouselAdapter;
    private RecyclerView offerRecyclerView;
    private OfferAdapter offerAdapter;

    private boolean isLoading = false;  // Track loading state
    private int currentPage = 0;       // Current page number (start from 1)
    private int totalItemsCount = 1;
    public OffersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("MethodsTag", "OffersFragment onCreate called");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MethodsTag", "OffersFragment onCreateView called");

        View rootView = inflater.inflate(R.layout.fragment_offer, container, false);


        // Initialize API Service
        offerService = ClientUtils.offerService;

        // Initialize UI Components
        offerCarousel = rootView.findViewById(R.id.offerCarousel);
        offerRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        offerAdapter = new OfferAdapter();
        offerRecyclerView.setAdapter(offerAdapter);
        // Fetch initial data
        fetchTopOffers(rootView);  // Top 5 offers for the carousel

        Button loadMoreButton = rootView.findViewById(R.id.loadMoreOffers);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        offerRecyclerView.setLayoutManager(layoutManager);
        offerRecyclerView.setNestedScrollingEnabled(false);


        fetchOffersWithPagination(currentPage, loadMoreButton);  // First page of offers

        // Setup Load More button click
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && offerAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                fetchOffersWithPagination(currentPage, loadMoreButton);
            }
        });

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

    private void fetchAllOffers(View rootView, LayoutInflater inflater) {
        ScrollView parentScrollView = rootView.findViewById(R.id.parentScrollView);
        ImageView filterIcon = rootView.findViewById(R.id.products_filter_icon);

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
        filterIcon.setOnClickListener(v -> showFilterDialog(inflater));

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

    private void showFilterDialog(LayoutInflater inflater) {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        // Inflate the filter layout
        View dialogView = inflater.inflate(R.layout.filter_products, null);
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