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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.Event;
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        eventService = ClientUtils.eventService;

        eventCarousel = rootView.findViewById(R.id.eventCarousel);
        eventRecyclerView = rootView.findViewById(R.id.eventRecyclerView);

        // Fetch data from API
        fetchTopFiveEvents(rootView);
        fetchAllEvents(rootView, inflater);

        return rootView;
    }

    // Calculate the height of all items in RecyclerView and set the height
    private void calculateAndSetRecyclerViewHeight(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View view = LayoutInflater.from(recyclerView.getContext())
                    .inflate(R.layout.event_card, recyclerView, false);

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
                    EventCarouselAdapter carouselAdapter = new EventCarouselAdapter(events);
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

    private void fetchAllEvents(View rootView, LayoutInflater inflater) {
        Log.d("EventsFragment", "fetchAllEvents started");

        // Initialize RecyclerView and filter icon
        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);
        ScrollView parentScrollView = rootView.findViewById(R.id.parentScrollView);

        // Fetch all events from the API
        Log.d("EventsFragment", "Fetching...");

        eventService.getAll().enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventDTO> events = response.body();
                    Log.d("EventsFragment", "Events fetched successfully. Count: " + events.size());

                    // Update RecyclerView adapter
                    EventAdapter eventAdapter = new EventAdapter(events);
                    eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    eventRecyclerView.setAdapter(eventAdapter);

                    // Listen for the first scroll event on the ScrollView
                    parentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        boolean isHeightCalculated = false;

                        @Override
                        public void onScrollChanged() {
                            if (!isHeightCalculated) {
                                calculateAndSetRecyclerViewHeight(eventRecyclerView);
                                isHeightCalculated = true;
                            }
                        }
                    });
                } else {
                    Log.e("EventsFragment", "Failed to fetch all events.");
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                Log.e("EventsFragment", "Error fetching all events", t);
            }
        });

        // Set an OnClickListener for the filter icon (if needed)
        filterIcon.setOnClickListener(v -> showFilterDialog(inflater));

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


}