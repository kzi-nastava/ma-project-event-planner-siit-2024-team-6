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
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.model.Event;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

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

        //TOP 5 EVENTS CAROUSEL

        //Top 5 carousel
        ViewPager2 eventCarousel = rootView.findViewById(R.id.eventCarousel);

        //List of top events
        List<Event> eventList = new ArrayList<>();

        Event event1 = new Event();
        event1.setPhotoID(R.drawable.event1);
        event1.setTitle("Ed Sheeran Concert");
        event1.setLocation("Bulevar Oslobodjenja 16");
        event1.setDescription("Join us for an unforgettable night of music with the famous singer-songwriter, Ed Sheeran. Enjoy his biggest hits live in concert!");
        event1.setDate(new java.util.Date(2024, 10, 10)); // Example date
        event1.setTime("19h");
        event1.setRating(4.5f);
        eventList.add(event1);

        Event event2 = new Event();
        event2.setPhotoID(R.drawable.event2);
        event2.setTitle("Rock Festival");
        event2.setLocation("Central Park, New York");
        event2.setDescription("Get ready for a rock extravaganza! Featuring top bands and artists, this festival promises an amazing atmosphere and great music.");
        event2.setDate(new java.util.Date(2024, 10, 15));
        event2.setTime("16h");
        event2.setRating(4.7f);
        eventList.add(event2);

        Event event3 = new Event();
        event3.setPhotoID(R.drawable.event3);
        event3.setTitle("Yoga Retreat");
        event3.setLocation("Sandy Beach Resort");
        event3.setDescription("Escape the hustle and bustle of city life with a rejuvenating yoga retreat by the beach. Relax, stretch, and meditate in a serene environment.");
        event3.setDate(new java.util.Date(2024, 11, 1));
        event3.setTime("8h");
        event3.setRating(4.2f);
        eventList.add(event3);

        Event event4 = new Event();
        event4.setPhotoID(R.drawable.event4);
        event4.setTitle("Food Festival");
        event4.setLocation("City Square");
        event4.setDescription("Taste the best local and international dishes at this exciting food festival! From savory to sweet, there’s something for everyone.");
        event4.setDate(new java.util.Date(2024, 10, 25));
        event4.setTime("10h");
        event4.setRating(4.0f);
        eventList.add(event4);

        Event event5 = new Event();
        event5.setPhotoID(R.drawable.event5);
        event5.setTitle("Art Exhibition");
        event5.setLocation("Modern Art Gallery");
        event5.setDescription("Explore a stunning collection of modern art at the prestigious Modern Art Gallery. Discover new perspectives and creativity from emerging artists.");
        event5.setDate(new java.util.Date(2024, 11, 5));
        event5.setTime("14h");
        event5.setRating(4.8f);
        eventList.add(event5);

        //Adapter for viewPager2
        EventCarouselAdapter carouselAdapter = new EventCarouselAdapter(eventList);
        eventCarousel.setAdapter(carouselAdapter);

        eventCarousel.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        eventCarousel.setPageTransformer(transformer);

        //ALL EVENTS

        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);


        RecyclerView eventRecyclerView = rootView.findViewById(R.id.eventRecyclerView);

        EventAdapter eventAdapter = new EventAdapter(eventList);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventRecyclerView.setAdapter(eventAdapter);

        // Dynamically calculate RecyclerView height
        eventRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            RecyclerView.Adapter adapter = eventRecyclerView.getAdapter();
            if (adapter != null) {
                int totalHeight = 0;
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    View item = eventRecyclerView.getLayoutManager().findViewByPosition(i);
                    if (item != null) {
                        totalHeight += item.getMeasuredHeight();
                    }
                }
                eventRecyclerView.getLayoutParams().height = totalHeight;
                eventRecyclerView.requestLayout();
            }
        });


        // Set an OnClickListener to open the BottomSheetDialog when the icon is clicked
        filterIcon.setOnClickListener(v -> {
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
        });

        return rootView;
    }


}