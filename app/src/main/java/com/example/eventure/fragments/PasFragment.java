package com.example.eventure.fragments;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.adapters.PASAdapter;
import com.example.eventure.adapters.PASCarouselAdapter;
import com.example.eventure.model.PAS;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasFragment newInstance(String param1, String param2) {
        PasFragment fragment = new PasFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_pas, container, false);

        // Top 5 Products Carousel

// Carousel for products and services
        ViewPager2 pasCarousel = rootView.findViewById(R.id.pasCarousel);
        ImageButton pasPrevButton = rootView.findViewById(R.id.pasPrevButton);
        ImageButton pasNextButton = rootView.findViewById(R.id.pasNextButton);
// List of top products and services
        List<PAS> pasList = new ArrayList<>();

// Product 1 - Live Music Band
        PAS product1 = new PAS();
        product1.setPhotoID(R.drawable.pas1);
        product1.setTitle("Live Music Band");
        product1.setDescription("Enhance your event with a live music band! From jazz to rock, enjoy a tailored musical experience that entertains all guests.");
        product1.setPrice(500); // Example price per event
        product1.setSale(400);
        pasList.add(product1);

// Product 2 - Custom Cakes
        PAS product2 = new PAS();
        product2.setPhotoID(R.drawable.pas2);
        product2.setTitle("Custom Cakes");
        product2.setDescription("Order beautifully crafted custom cakes for any occasion, made to match your theme and taste preferences.");
        product2.setPrice(150); // Example price per cake
        product2.setSale(0);
        pasList.add(product2);

// Product 3 - Portable Bluetooth Speakers
        PAS product3 = new PAS();
        product3.setPhotoID(R.drawable.pas3);
        product3.setTitle("Portable Bluetooth Speakers");
        product3.setDescription("Take the party anywhere with our high-quality portable Bluetooth speakers, delivering crisp sound and deep bass.");
        product3.setPrice(120); // Example price per unit
        product3.setSale(50);
        pasList.add(product3);

// Product 4 - Event Decorations
        PAS product4 = new PAS();
        product4.setPhotoID(R.drawable.pas4);
        product4.setTitle("Event Decorations");
        product4.setDescription("Transform your venue with customized event decorations, including floral arrangements, lighting, and themed setups.");
        product4.setPrice(300); // Example price per setup
        pasList.add(product4);

// Product 5 - Photography Service
        PAS product5 = new PAS();
        product5.setPhotoID(R.drawable.pas5);
        product5.setTitle("Photography Service");
        product5.setDescription("Capture the best moments with a professional photography service, perfect for weddings, parties, and corporate events.");
        product5.setPrice(400); // Example price per session
        pasList.add(product5);

// Adapter for ViewPager2
        PASCarouselAdapter carouselAdapter = new PASCarouselAdapter(pasList);
        pasCarousel.setAdapter(carouselAdapter);

        pasCarousel.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        pasCarousel.setPageTransformer(transformer);
// Arrow Button Functionality
        pasPrevButton.setOnClickListener(v -> {
            int currentItem = pasCarousel.getCurrentItem();
            if (currentItem > 0) {
                pasCarousel.setCurrentItem(currentItem - 1, true);
            }
        });

        pasNextButton.setOnClickListener(v -> {
            int currentItem = pasCarousel.getCurrentItem();
            if (currentItem < carouselAdapter.getItemCount() - 1) {
                pasCarousel.setCurrentItem(currentItem + 1, true);
            }
        });


        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.products_filter_icon);

        // RecyclerView for listing all PAS items
        RecyclerView pasRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        PASAdapter pasAdapter = new PASAdapter(pasList);
        pasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pasRecyclerView.setAdapter(pasAdapter);

        ScrollView parentScrollView = rootView.findViewById(R.id.parentScrollView);

        // Listen for the first scroll event on the ScrollView
        parentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            boolean isHeightCalculated = false;

            @Override
            public void onScrollChanged() {
                if (!isHeightCalculated) {
                    calculateAndSetRecyclerViewHeight(pasRecyclerView);
                    isHeightCalculated = true;
                }
            }
        });


        // Set an OnClickListener to open the BottomSheetDialog when the icon is clicked
        filterIcon.setOnClickListener(v -> {
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
        });


//        // Apply strikethrough to the old price
//        View productCard1 = rootView.findViewById(R.id.product1);
//        TextView oldPriceTextView = productCard1.findViewById(R.id.product_price);
//        oldPriceTextView.setPaintFlags(oldPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//
//        //Remove sale tag and sale price
//        View productCard2 = rootView.findViewById(R.id.product2);
//        TextView saleTag = productCard2.findViewById(R.id.sale_tag);
//        saleTag.setVisibility(View.GONE);
//        View salePrice = productCard2.findViewById(R.id.sale_price_layout);
//        salePrice.setVisibility(View.GONE);

        return rootView;
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
}