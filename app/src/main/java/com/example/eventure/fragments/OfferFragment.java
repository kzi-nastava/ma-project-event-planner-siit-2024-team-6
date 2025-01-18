package com.example.eventure.fragments;

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

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.adapters.OfferCarouselAdapter;
import com.example.eventure.model.Offer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OfferFragment() {
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
    public static OfferFragment newInstance(String param1, String param2) {
        OfferFragment fragment = new OfferFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_offer, container, false);

        // Top 5 Products Carousel

// Carousel for products and services
        ViewPager2 offerCarousel = rootView.findViewById(R.id.offerCarousel);
        ImageButton offerPrevButton = rootView.findViewById(R.id.offerPrevButton);
        ImageButton offerNextButton = rootView.findViewById(R.id.offerNextButton);
// List of top products and services
        List<Offer> offerList = new ArrayList<>();



// Adapter for ViewPager2
        OfferCarouselAdapter carouselAdapter = new OfferCarouselAdapter(offerList);
        offerCarousel.setAdapter(carouselAdapter);

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


        // Find the filter icon
        ImageView filterIcon = rootView.findViewById(R.id.products_filter_icon);

        // RecyclerView for listing all offer items
        RecyclerView offerRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        OfferAdapter offerAdapter = new OfferAdapter(offerList);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offerRecyclerView.setAdapter(offerAdapter);

        ScrollView parentScrollView = rootView.findViewById(R.id.parentScrollView);

        // Listen for the first scroll event on the ScrollView
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