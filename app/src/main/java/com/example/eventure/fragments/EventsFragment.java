package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.clients.EventTypeService;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.eventure.clients.EventService;
import com.example.eventure.clients.ClientUtils;
import com.google.android.material.datepicker.MaterialDatePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    private EventService eventService;
    private EventTypeService eventTypeService;
    private ViewPager2 eventCarousel;
    private EventCarouselAdapter carouselAdapter;
    private RecyclerView eventRecyclerView;
    private TextView emptyEvents;
    private Button loadMoreButton;
    private EventAdapter eventAdapter;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalItemsCount = 1;

    //Filter
    private boolean isFilterMode = false;
    private String currentFilterType = null;
    private String currentFilterStartDate = null;
    private String currentFilterEndDate = null;
    private List<String> eventTypes = new ArrayList<>();
    private String selectedEventType = "";

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("MethodsTag", "EventsFragment onCreate called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MethodsTag", "EventsFragment onCreateView called");

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        eventService = ClientUtils.eventService;
        eventTypeService = ClientUtils.eventTypeService;
        eventCarousel = rootView.findViewById(R.id.eventCarousel);
        eventRecyclerView = rootView.findViewById(R.id.eventRecyclerView);
        emptyEvents = rootView.findViewById(R.id.emptyEvents);
        eventAdapter = new EventAdapter();
        eventRecyclerView.setAdapter(eventAdapter);
        currentPage = 0;
        totalItemsCount = 1;
        // Fetch data from API
        fetchTopFiveEvents(rootView);
        //fetchAllEvents(rootView, inflater);
        // Fetch the first page
        loadMoreButton = rootView.findViewById(R.id.loadMoreEvents);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setNestedScrollingEnabled(false);
        fetchAllEventsWithPagination(currentPage, loadMoreButton);

        // Set up button click listener for loading more
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading && eventAdapter.getItemCount() < totalItemsCount) {
                currentPage++;
                if (isFilterMode) {
                    fetchFilteredEvents(currentFilterType, currentFilterStartDate, currentFilterEndDate, currentPage);
                } else {
                    fetchAllEventsWithPagination(currentPage, loadMoreButton);
                }
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

        ImageView filterIcon = rootView.findViewById(R.id.events_filter_icon);
        filterIcon.setOnClickListener(v -> showFilterDialog(inflater));

        return rootView;
    }

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

                        // show/hide empty text if there are no events found
                        toggleEmptyEvents();
                        // Show or hide the Load More button based on the remaining pages
                        updateLoadMoreVisibility();
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
        // Create BottomSheetDialog with full-screen style
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetFullScreen);
        View dialogView = inflater.inflate(R.layout.filter_events, null);
        bottomSheetDialog.setContentView(dialogView);

        // Access the BottomSheetBehavior and force full screen
        View bottomSheet = dialogView.getParent() instanceof View ? (View) dialogView.getParent() : null;
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            behavior.setPeekHeight(screenHeight);
        }

        // Close button
        ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Initialize Spinner
        Spinner eventTypeSpinner = dialogView.findViewById(R.id.types_event);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, eventTypes);
        eventTypeSpinner.setAdapter(adapter);

        // Setup MaterialDatePicker
        TextView startDateText = dialogView.findViewById(R.id.start_date_text);
        TextView endDateText = dialogView.findViewById(R.id.end_date_text);
        Button chooseDateButton = dialogView.findViewById(R.id.choose_date_button);

        final long[] selectedStartDate = {0L};
        final long[] selectedEndDate = {0L};
        chooseDateButton.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Select a date range");

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedStartDate[0] = selection.first;
                selectedEndDate[0] = selection.second;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                startDateText.setText("From:  " + formatDate(selectedStartDate[0]));
                endDateText.setText("To:       " + formatDate(selectedEndDate[0]));
            });
        });

        // RESET button
        Button resetButton = dialogView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            selectedStartDate[0] = 0L;
            selectedEndDate[0] = 0L;

            startDateText.setText("From: Not selected");
            endDateText.setText("To: Not selected");

            eventTypeSpinner.setSelection(0);
        });

        //APPLY button
        Button applyButton = dialogView.findViewById(R.id.filter_button);
        applyButton.setOnClickListener(v -> {
            String selectedType = eventTypeSpinner.getSelectedItem().toString();
            Long start = selectedStartDate[0] == 0L ? null : selectedStartDate[0];
            Long end = selectedEndDate[0] == 0L ? null : selectedEndDate[0];
            // If no params were given for filter when applying just close dialog
            if ("Select Event Type".equals(selectedType) && start == null && end == null) {
                bottomSheetDialog.dismiss();
                return;
            }
            bottomSheetDialog.dismiss();
            isFilterMode = true;
            currentPage = 0;
            currentFilterType = "Select Event Type".equals(selectedType) ? null : selectedType;
            currentFilterStartDate = formatDate(start);
            currentFilterEndDate = formatDate(end);

            eventAdapter.clearEvents(); // Clear old data
            fetchFilteredEvents(currentFilterType, currentFilterStartDate, currentFilterEndDate, currentPage);
        });

        bottomSheetDialog.show();
    }
    private void fetchFilteredEvents(String eventType, String startDate, String endDate, int page) {
        isLoading = true; // Prevent multiple calls at once

        eventService.getFilteredEvents(eventType, startDate, endDate, page, 2)
                .enqueue(new Callback<PagedResponse<EventDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<EventDTO>> call, Response<PagedResponse<EventDTO>> response) {
                        isLoading = false;

                        if (response.isSuccessful() && response.body() != null) {
                            PagedResponse<EventDTO> pagedResponse = response.body();
                            totalItemsCount = pagedResponse.getTotalElements();

                            List<EventDTO> filteredEvents = pagedResponse.getContent();
                            if (filteredEvents != null && !filteredEvents.isEmpty()) {
                                Log.d("EventsTag", "Fetched " + filteredEvents.size() + " filtered events on page: " + page);
                                eventAdapter.addEvents(filteredEvents); // Append new items
                                eventAdapter.notifyItemRangeInserted(
                                        eventAdapter.getItemCount() - filteredEvents.size(),
                                        filteredEvents.size()
                                );
                            } else {
                                Log.d("EventsTag", "No filtered events on page: " + page);
                            }
                        } else {
                            Log.d("EventsTag", "Failed to fetch filtered events: " + response.code());
                        }
                        // show/hide empty text if there are no events found
                        toggleEmptyEvents();
                        // Show or hide the Load More button based on the remaining pages
                        updateLoadMoreVisibility();

                    }

                    @Override
                    public void onFailure(Call<PagedResponse<EventDTO>> call, Throwable t) {
                        isLoading = false;
                        Log.e("EventsTag", "Error loading filtered events: " + t.getMessage());
                    }
                });
    }
    public void fetchEventTypes(Callback<List<String>> callback) {
        eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
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

    private void updateLoadMoreVisibility() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) eventRecyclerView.getLayoutParams();
        if (eventAdapter.getItemCount() < totalItemsCount) {
            loadMoreButton.setVisibility(View.VISIBLE);
            params.bottomMargin = 0;
        } else {
            loadMoreButton.setVisibility(View.GONE);
            params.bottomMargin = (int) (70 * getResources().getDisplayMetrics().density);
        }
        eventRecyclerView.setLayoutParams(params);
    }
    private void toggleEmptyEvents() {
        if (eventAdapter.getItemCount() == 0) {
            emptyEvents.setVisibility(View.VISIBLE);
        } else {
            emptyEvents.setVisibility(View.GONE);
        }
    }
    private String formatDate(Long millis) {
        if (millis == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
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