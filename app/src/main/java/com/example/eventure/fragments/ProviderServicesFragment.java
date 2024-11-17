package com.example.eventure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.model.Event;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ProviderServicesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProviderServicesFragment() {
        // Required empty public constructor
    }
    public static ProviderServicesFragment newInstance(String param1, String param2) {
        ProviderServicesFragment fragment = new ProviderServicesFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_proivder_services, container, false);

        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.filter_icon);

        // Set an OnClickListener to open the BottomSheetDialog when the icon is clicked
        filterIcon.setOnClickListener(v -> {
            // Create a BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            // Inflate the filter layout
            View dialogView = inflater.inflate(R.layout.filter_provider_services, null);
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
