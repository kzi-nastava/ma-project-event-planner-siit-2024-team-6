package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.adapters.ReviewAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ReactionDTO;
import com.example.eventure.model.PagedResponse;
import com.example.eventure.model.Provider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyDetailsDialog extends DialogFragment {

    private static final String ARG_PROVIDER = "provider";
    private Provider provider;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private boolean isLoadingReviews = false;
    private int currentPage = 0;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;

    public static CompanyDetailsDialog newInstance(Provider provider) {
        CompanyDetailsDialog fragment = new CompanyDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROVIDER, provider);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.provider_company_view, container, false);

        if (getArguments() != null) {
            provider = (Provider) getArguments().getSerializable(ARG_PROVIDER);
        }

        if (provider == null) {
            dismiss();
            return view;
        }

        setupUI(view);

        Button btnReport = view.findViewById(R.id.btn_report);
        btnReport.setOnClickListener(v -> {
            ReportDialog reportDialog = ReportDialog.newInstance(provider.getId());
            reportDialog.show(getParentFragmentManager(), "report_dialog");
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());

        // Set up photo carousel
        ViewPager2 photoCarousel = view.findViewById(R.id.photo_carousel);
        if (provider.getCompanyPhotos() != null && provider.getCompanyPhotos().length > 0) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), Arrays.asList(provider.getCompanyPhotos()));
            photoCarousel.setAdapter(adapter);
        } else {
            photoCarousel.setVisibility(View.GONE);
        }

        TextView tvCompanyName = view.findViewById(R.id.tv_company_name);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvEmail = view.findViewById(R.id.tv_company_email);
        TextView tvAddress = view.findViewById(R.id.tv_company_address);
        TextView workingHours = view.findViewById(R.id.tv_working_hours);

        tvCompanyName.setText(provider.getCompanyName());
        tvDescription.setText(provider.getDescription());
        tvEmail.setText("Email: "+provider.getCompanyEmail());
        tvAddress.setText("Address: "+provider.getCompanyAddress());
        workingHours.setText(provider.getOpeningTime()+" - "+provider.getClosingTime()+"h");

        rvReviews = view.findViewById(R.id.recycler_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);

        rvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoadingReviews && !isLastPage && layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3
                            && firstVisibleItemPosition >= 0) {
                        loadReviewsPage(currentPage + 1);
                    }
                }
            }
        });

        loadReviewsPage(0);

    }
    private void loadReviewsPage(int page) {
        if (provider == null) return; // Safety check

        isLoadingReviews = true;

        Call<PagedResponse<ReactionDTO>> call = ClientUtils.reactionService.getProviderReactions(provider.getId(), page, PAGE_SIZE);
        call.enqueue(new Callback<PagedResponse<ReactionDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<ReactionDTO>> call, Response<PagedResponse<ReactionDTO>> response) {
                isLoadingReviews = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<ReactionDTO> newReviews = response.body().getContent();

                    if (page == 0) {
                        reviewAdapter = new ReviewAdapter(new ArrayList<>(newReviews));
                        rvReviews.setAdapter(reviewAdapter);
                    } else {
                        reviewAdapter.addReviews(newReviews);
                    }

                    currentPage = page;
                    isLastPage = response.body().isLast();
                } else {
                    // Show some error or ignore
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<ReactionDTO>> call, Throwable t) {
                isLoadingReviews = false;
                View rootView = getView();
                if (rootView != null) {
                    Snackbar.make(rootView, "Failed to load reviews: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
