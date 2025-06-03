package com.example.eventure.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.clients.AuthService;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.OfferService;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.dto.ProviderDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;
import com.example.eventure.model.Provider;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferDetailsDialog extends DialogFragment {

    private Offer offer;
    private ImageButton btnFavorite;
    private Button btnBook;
    private boolean isFavorited;

    public static OfferDetailsDialog newInstance(Offer offer) {
        OfferDetailsDialog fragment = new OfferDetailsDialog();
        Bundle args = new Bundle();
        args.putParcelable("offer", offer);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.offer_details, container, false);

        if (getArguments() != null) {
            offer = getArguments().getParcelable("offer");
        }

        if (offer == null) {
            dismiss();
            return view;
        }

        setupUI(view);
        populateUI(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog width to 95% of screen width, height wrap content
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Optional: Make background transparent if desired
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());


        btnFavorite = view.findViewById(R.id.btn_favorite);
        btnFavorite.setOnClickListener(v -> {
            if (!ClientUtils.getAuthService().isLoggedIn()){
                Snackbar.make(view, "User error: " + "You must be logged to like the offer.", Snackbar.LENGTH_SHORT).show();
            }else{
                isFavorited = !isFavorited;
                if (isFavorited) {
                    btnFavorite.setImageResource(R.drawable.heart_filled_icon);
                    // Add to favourites
                    ClientUtils.offerService.addOfferToFavourites(offer.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(view, "Added to favourites", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "Failed to add: " + response.code(), Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Snackbar.make(view, "Network error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    btnFavorite.setImageResource(R.drawable.heart_icon);
                    // Remove from favourites
                    ClientUtils.offerService.removeOfferFromFavourites(offer.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Snackbar.make(view, "Removed from favourites", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "Failed to remove: " + response.code(), Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Snackbar.make(view, "Network error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        isFavorited = false;

        // Check to see if the offer is already in users favourites:
        if (ClientUtils.getAuthService().isLoggedIn()){
            ClientUtils.offerService.isOfferFavourited(offer.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean isFavourited = response.body();
                        if (isFavourited) {
                            // Show filled heart icon or other UI indicator
                            btnFavorite.setImageResource(R.drawable.heart_filled_icon);
                            isFavorited = true;
                        } else {
                            // Show unfilled heart icon
                            btnFavorite.setImageResource(R.drawable.heart_icon);
                        }
                    } else {
                        Snackbar.make(view, "Failed to check favourite status", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        btnBook = view.findViewById(R.id.btn_book);
        btnBook.setOnClickListener(v -> {
            if (offer.getType().equals("Service")) {
                // TODO: Implement booking logic for services
            } else {
                // TODO: Implement booking logic for products
            }
        });

        ImageButton btnAccount = view.findViewById(R.id.provider_icon);
        btnAccount.setOnClickListener(v -> {
            Call<ProviderDTO> call = ClientUtils.offerService.getProviderByOfferId(offer.getId());

            call.enqueue(new Callback<ProviderDTO>() {
                @Override
                public void onResponse(Call<ProviderDTO> call, Response<ProviderDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ProviderDTO providerDTO = response.body();
                        Provider p = new Provider(providerDTO);
                        CompanyDetailsDialog providerDialog = CompanyDetailsDialog.newInstance(p);
                        providerDialog.show(requireActivity().getSupportFragmentManager(), "ProviderDetailsDialog");
                    }
                }

                @Override
                public void onFailure(Call<ProviderDTO> call, Throwable t) {
                    Snackbar.make(requireView(), "Network error. Please try again.", Snackbar.LENGTH_LONG).show();
                    Log.e("OfferDetailsDialog", "Failed to fetch provider: " + t.getMessage());
                }
            });
        });

    }

    @SuppressLint("ResourceAsColor")
    private void populateUI(View view) {
        TextView title = view.findViewById(R.id.offer_title);
        title.setText(offer.getName());

        TextView description = view.findViewById(R.id.details_text);
        String text = offer.getDescription();
        if (offer.getSpecifics() != null) {
            text += "\n" + offer.getSpecifics();
        }
        description.setText(text);

        TextView eventTypes = view.findViewById(R.id.event_types_text);
        StringBuilder sb = new StringBuilder();
        for (EventType et : offer.getEventTypes()) {
            sb.append(et.getName()).append("\n");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        eventTypes.setText(sb.toString());

        TextView category = view.findViewById(R.id.category_text);
        category.setText(offer.getCategory());

        TextView priceText = view.findViewById(R.id.price_text);
        String price = offer.getPrice().toString() + "$";
        String sale = "";
        if (offer.getSale() != null && offer.getSale() > 0.0) {
            sale = "  " + offer.getSale() + "$";
        }
        if (sale.isEmpty()) {
            priceText.setText(price);
        } else {
            String end = price + sale;
            SpannableString spannable = new SpannableString(end);
            int startColor = end.indexOf(sale);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink)), startColor, startColor + sale.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int strikeStart = end.indexOf(price);
            spannable.setSpan(new StrikethroughSpan(), strikeStart, strikeStart + price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceText.setText(spannable);
        }

        TextView available = view.findViewById(R.id.availability_text);
        if (offer.getIsAvailable()) {
            available.setText("Available.");
        } else {
            available.setText("Currently unavailable.");
            btnBook.setActivated(false);
        }

        ViewPager2 imageCarousel = view.findViewById(R.id.image_carousel);
        if (offer.getPhotos() != null && !offer.getPhotos().isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), offer.getPhotos());
            imageCarousel.setAdapter(adapter);
        }

        if (offer.getType().equals("Service")) {
            populateServiceUI(view);
        } else {
            populateProductUI(view);
        }
    }

    private void populateProductUI(View view) {
        TextView durationLabel = view.findViewById(R.id.duration_label);
        durationLabel.setVisibility(View.GONE);
        TextView duration = view.findViewById(R.id.duration_text);
        duration.setVisibility(View.GONE);
        TextView deadline = view.findViewById(R.id.deadline_text);
        deadline.setVisibility(View.GONE);
        btnBook = view.findViewById(R.id.btn_book);
        btnBook.setText("BUY");
    }

    private void populateServiceUI(View view) {
        TextView duration = view.findViewById(R.id.duration_text);
        if (offer.getPreciseDuration() == 0) {
            duration.setText(offer.getMinDuration() + " - " + offer.getMaxDuration() + " minutes");
        } else {
            duration.setText(offer.getPreciseDuration() + " minutes");
        }

        TextView cancelDeadline = view.findViewById(R.id.deadline_text);
        cancelDeadline.setText("Cancelation available up until " + offer.getLatestCancellation() + "h before the booking.");
    }

    private void makeReviewUIVisible(View view) {
        TextView reviewLabel = view.findViewById(R.id.review_label);
        reviewLabel.setVisibility(View.VISIBLE);
        EditText rating = view.findViewById(R.id.review_rating);
        rating.setVisibility(View.VISIBLE);
        EditText review = view.findViewById(R.id.review_input);
        review.setVisibility(View.VISIBLE);
        Button submit = view.findViewById(R.id.btn_submit_review);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(v -> {
            // TODO: Submit review logic
        });
    }
}
