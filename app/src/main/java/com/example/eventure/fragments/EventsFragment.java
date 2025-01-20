package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.Event;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import com.example.eventure.clients.EventService;
import com.example.eventure.clients.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EventService eventService;
    private ViewPager2 eventCarousel;
    private EventCarouselAdapter carouselAdapter;
    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;

    private boolean isLoading = false;  // Track loading state
    private int currentPage = 0;       // Current page number
    private int totalItemsCount = 1;    // Total pages available
    private boolean isInitialized = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("MethodsTag", "EventsFragment onCreate called");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MethodsTag", "EventsFragment onCreateView called");

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        eventService = ClientUtils.eventService;

        eventCarousel = rootView.findViewById(R.id.eventCarousel);
        eventRecyclerView = rootView.findViewById(R.id.eventRecyclerView);
        eventAdapter = new EventAdapter();
        eventRecyclerView.setAdapter(eventAdapter);
        currentPage = 0;
        totalItemsCount = 1;
        // Fetch data from API
        fetchTopFiveEvents(rootView);
        //fetchAllEvents(rootView, inflater);
        // Fetch the first page
        Button loadMoreButton = rootView.findViewById(R.id.loadMoreEvents);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setNestedScrollingEnabled(false);
        fetchAllEventsWithPagination(currentPage, loadMoreButton);

        // Set up button click listener for loading more
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && eventAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                fetchAllEventsWithPagination(currentPage, loadMoreButton);
            }
        });


        return rootView;
    }

//    private void setupPagination(View rootView) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setStackFromEnd(false); // Prevent auto-scrolling to the end
//        layoutManager.setReverseLayout(false);
//        eventRecyclerView.setLayoutManager(layoutManager);
//        eventRecyclerView.setNestedScrollingEnabled(false);
//
//        // Set up the scroll listener
//        eventRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (!isInitialized || isLoading) return;
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager == null || eventAdapter == null) return;
//                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                int totalItemCount = eventAdapter.getItemCount();
//
//                Log.d("EventsTag", "Last visible item: " + lastVisibleItemPosition);
//                Log.d("EventsTag", "Total item count: " + totalItemCount);
//
//                // Check if the user scrolled to the last item
//                if (lastVisibleItemPosition == totalItemCount - 1 && currentPage < totalPageCount) {
//                    Log.d("EventsTag", "User scrolled to the end. Fetching next page...");
//                    currentPage++;
//                    fetchAllEventsWithPagination(currentPage);
//                }
//            }
//        });
//
//        // Fetch the first page and initialize the list
//        fetchAllEventsWithPagination(currentPage);
//    }

    private void fetchAllEventsWithPagination(int page, Button loadMoreButton) {
        if (isLoading) return;

        isLoading = true; // Set loading state
        Log.d("EventsTag", "Fetching page: " + page);

        eventService.getPagedEvents(page, 5).enqueue(new Callback<PagedResponse<EventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<EventDTO>> call, Response<PagedResponse<EventDTO>> response) {
                isLoading = false; // Reset loading state

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<EventDTO> pagedResponse = response.body();
                    totalItemsCount = pagedResponse.getTotalElements();
                    Log.d("EventsTag", "Total " + totalItemsCount);

                    if (pagedResponse.getContent() != null && !pagedResponse.getContent().isEmpty()) {
                        List<EventDTO> newEvents = pagedResponse.getContent();
                        Log.d("EventsTag", "Fetched " + newEvents.size() + " events from page: " + page);
                        Log.d("EventsTag", "DODAO EVENTS");

                        eventAdapter.addEvents(newEvents); // Append new events to the list
                        eventAdapter.notifyItemRangeInserted(
                                eventAdapter.getItemCount() - newEvents.size(),
                                newEvents.size()
                        );

                        // Show or hide the Load More button based on the remaining pages
                        if (eventAdapter.getItemCount() < totalItemsCount) {
                            loadMoreButton.setVisibility(View.VISIBLE);
                        } else {
                            loadMoreButton.setVisibility(View.GONE); // Hide if no more pages
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) eventRecyclerView.getLayoutParams();
                            params.bottomMargin = (int) getResources().getDisplayMetrics().density * 70; // Convert 70dp to pixels
                            eventRecyclerView.setLayoutParams(params);
                        }
                    }
                } else {
                    Log.e("EventsFragment", "Failed to fetch events. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<EventDTO>> call, Throwable t) {
                isLoading = false; // Reset loading state
                Log.e("EventsFragment", "Error fetching events", t);
            }
        });
    }
    private void fetchTopFiveEvents(View rootView) {
        Log.d("EventsFragment", "fetchTopFiveEvents started");

        // Initialize UI components for the carousel
//        ViewPager2 eventCarousel = rootView.findViewById(R.id.eventCarousel);
        ImageButton prevButton = rootView.findViewById(R.id.prevButton);
        ImageButton nextButton = rootView.findViewById(R.id.nextButton);
        Log.d("EventsFragment", "Fetching top five events...");

        // Fetch top 5 events from the API
        eventService.getTopFive().enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<EventDTO> events = response.body();
                    Log.d("EventsFragment", "Top five events fetched successfully. Count: " + events.size());

                    // Update carousel adapter
                    carouselAdapter = new EventCarouselAdapter(events);
                    eventCarousel.setAdapter(carouselAdapter);

                    // Apply carousel transformations
                    eventCarousel.setOffscreenPageLimit(3);
                    CompositePageTransformer transformer = new CompositePageTransformer();
                    transformer.addTransformer(new MarginPageTransformer(40));
                    transformer.addTransformer((page, position) -> {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    });
                    eventCarousel.setPageTransformer(transformer);

                    // Arrow Button Functionality
                    prevButton.setOnClickListener(v -> {
                        int currentItem = eventCarousel.getCurrentItem();
                        if (currentItem > 0) {
                            eventCarousel.setCurrentItem(currentItem - 1, true);
                        }
                    });

                    nextButton.setOnClickListener(v -> {
                        int currentItem = eventCarousel.getCurrentItem();
                        if (currentItem < carouselAdapter.getItemCount() - 1) {
                            eventCarousel.setCurrentItem(currentItem + 1, true);
                        }
                    });
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("EventsFragment", "Failed to fetch events. Response code: " + response.code());
                        Log.e("EventsFragment", "Response error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("EventsFragment", "Error reading errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                Log.e("EventsFragment", "Error fetching top 5 events", t);
            }
        });
    }

    private void showFilterDialog(LayoutInflater inflater) {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        // Inflate the filter layout
        View dialogView = inflater.inflate(R.layout.filter_events, null);
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
        Log.e("MethodsTag", "EventsFragment onStart called");

        super.onStart();
        // Logic to execute when the fragment becomes visible
    }

    @Override
    public void onResume() {
        Log.e("MethodsTag", "EventsFragment onResume called");

        super.onResume();
        // Logic to execute when the fragment is interactable
    }

    @Override
    public void onPause() {
        Log.e("MethodsTag", "EventsFragment onPause called");

        super.onPause();
        // Save changes or pause actions
    }

    @Override
    public void onStop() {
        Log.e("MethodsTag", "EventsFragment onStop called");

        super.onStop();
        // Logic to execute when the fragment is no longer visible
    }

    @Override
    public void onDestroyView() {
        Log.e("MethodsTag", "EventsFragment onDestoryView called");

        super.onDestroyView();
        this.onDestroy();
        // Clean up resources related to the view
    }

    @Override
    public void onDestroy() {
        Log.e("MethodsTag", "EventsFragment onDestroy called");

        super.onDestroy();
        // Final cleanup logic
    }

}