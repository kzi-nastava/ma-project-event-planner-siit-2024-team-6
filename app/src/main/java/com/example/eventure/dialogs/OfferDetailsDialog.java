package com.example.eventure.dialogs;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.app.Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.activities.ChatActivity;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.clients.AuthService;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.OfferService;
import com.example.eventure.dto.NewNotificationDTO;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.dto.NewReactionDTO;
import com.example.eventure.dto.NotificationDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.dto.ReactionDTO;
import com.example.eventure.dto.UserDTO;
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
    private Button btnBook, btnContact;
    private boolean isFavorited;
    private UserDTO provider;

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
        checkForImmediateReview(view);
        loadProviderInfo(view);
        return view;
    }

    private void loadProviderInfo(View view){
        Call<UserDTO> call = ClientUtils.offerService.getProvider(offer.getId());
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provider = response.body();
                } else {
                    Snackbar.make(view, "Cannot access provider's info.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Snackbar.make(view, "Error: "+t.getMessage(), Snackbar.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
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

    private void checkForImmediateReview(View view){
        if (offer.getType().equals("Product")){
            Call<Boolean> call2 = ClientUtils.offerService.isOfferPurchased(offer.getId());

            call2.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call2, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean isPurchased = response.body();
                        if (Boolean.TRUE.equals(isPurchased)) {
                            makeReviewUIVisible(view);
                        }
                    } else {
                        //
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call2, Throwable t) {
                }
            });
        }else{
            Call<Boolean> call = ClientUtils.reservationService.isOfferReservedByUser(offer.getId());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean isReserved = response.body();
                        if (Boolean.TRUE.equals(isReserved)) {
                            makeReviewUIVisible(view);
                        }
                    } else {
                        //Toast.makeText(context, "Failed to check reservation.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                }
            });
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

        if(ClientUtils.getAuthService().isLoggedIn() && ClientUtils.getAuthService().getRole().equals("ROLE_ORGANIZER")){
            btnBook.setOnClickListener(v -> {
                if (offer.getType().equals("Service")) {

                    BookServiceDialog dialog = new BookServiceDialog(offer.getId(),offer.getPreciseDuration());
                    dialog.setBookingResultListener(success -> {
                        if (success) {
                            // Booking was successful
                            Log.d("BookingTag", "Booking completed");
                            View rootView = getView();
                            if (rootView != null) {
                                makeReviewUIVisible(rootView);
                            } else {
                                Log.e("BookingTag", "Root view is null, can't make review UI visible");
                            }

                        } else {
                            Log.d("BookingTag", "Booking was not completed");
                        }
                    });
                    dialog.show(getParentFragmentManager(), "book_service");
                } else {
                    Log.d("BLAH", "Booking was not completed");
                    BuyProductDialog dialog = new BuyProductDialog(offer.getId());
                    dialog.setBookingResultListener(success -> {
                        if (success) {
                            // Booking was successful
                            Log.d("BookingTag", "Booking completed");
                            View rootView = getView();
                            if (rootView != null) {
                                makeReviewUIVisible(rootView);
                            } else {
                                Log.e("BookingTag", "Root view is null, can't make review UI visible");
                            }

                        } else {
                            Log.d("BookingTag", "Booking was not completed");
                        }
                    });
                    dialog.show(getParentFragmentManager(), "BuyProductDialog");
                }
            });
        }else{
            btnBook.setVisibility(View.INVISIBLE);
        }

        ImageButton btnAccount = view.findViewById(R.id.provider_icon);
        btnAccount.setOnClickListener(v -> {
            if(provider == null){
                Snackbar.make(view, "Cannot access provider's info. Try again later.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Provider p = new Provider(provider);
            CompanyDetailsDialog providerDialog = CompanyDetailsDialog.newInstance(p);
            providerDialog.show(requireActivity().getSupportFragmentManager(), "ProviderDetailsDialog");

        });

        btnContact = view.findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v -> {
            if (!ClientUtils.getAuthService().isLoggedIn()) {
                Snackbar.make(view, "You must be logged in to contact the organizer.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(provider == null){
                Snackbar.make(view, "Cannot access provider's info. Try again later.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            ClientUtils.chatService.findChat(provider.getId()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        int chatId = response.body();
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("chatId", chatId);
                        intent.putExtra("userName", provider.getName() + " "+ provider.getLastname());
                        intent.putExtra("userImage", provider.getPhotoUrl());
                        startActivity(intent);
                    } else {
                        Snackbar.make(view, "Cannot access a chat with yourself.", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Snackbar.make(view, "Error: "+t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    t.printStackTrace();
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
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        TextView ratingValue = view.findViewById(R.id.rating_value);

        ClientUtils.offerService.getRating(offer.getId()).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double rating = response.body(); // You can change this to Double if needed
                    if (rating == 0.0) {
                        ratingBar.setVisibility(View.GONE);
                        ratingValue.setText("No ratings yet");
                    } else {
                        ratingBar.setRating((float) rating);
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingValue.setText("");
                    }
                } else {
                    ratingValue.setText("No rating");
                    ratingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                ratingValue.setText("No rating");
                ratingBar.setVisibility(View.GONE);
            }
        });

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
        RatingBar ratingBar = view.findViewById(R.id.review_rating_bar);
        ratingBar.setVisibility(View.VISIBLE);
        EditText review = view.findViewById(R.id.review_input);
        review.setVisibility(View.VISIBLE);
        Button submit = view.findViewById(R.id.btn_submit_review);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(v -> {
            String reviewText = review.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (rating == 0.0f && reviewText.isEmpty()) {
                Snackbar.make(v, "Please select a rating or enter a review before submitting.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            NewReactionDTO reaction = new NewReactionDTO(reviewText, Math.round(rating), null, offer.getId());


            Call<ReactionDTO> call = ClientUtils.reactionService.addReaction(reaction);
            call.enqueue(new Callback<ReactionDTO>() {
                @Override
                public void onResponse(Call<ReactionDTO> call, Response<ReactionDTO> response) {
                    if (response.isSuccessful()) {
                        Snackbar.make(v, "Reaction submitted successfully!", Snackbar.LENGTH_LONG).show();
                        sendReviewNotification(response.body(), offer.getName(), offer.getId(), v);

                    } else {
                        Snackbar.make(v, "Failed to submit reaction.", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ReactionDTO> call, Throwable t) {
                    Snackbar.make(v, "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
    private void fetchProviderByOfferId(int offerId, Callback<UserDTO> callback) {
        Call<UserDTO> call = ClientUtils.offerService.getProvider(offerId);
        call.enqueue(callback);
    }

    private void sendNotificationToProvider(NewNotificationDTO notification, Callback<NotificationDTO> callback) {
        Call<NotificationDTO> call = ClientUtils.notificationService.addNotification(notification);
        call.enqueue(callback);
    }

    private void sendReviewNotification(ReactionDTO reaction, String offerName, int offerId, View v) {
        fetchProviderByOfferId(offerId, new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO provider = response.body();
                    String notificationText = createNotificationText(reaction, offerName);
                    NewNotificationDTO notification = new NewNotificationDTO();
                    notification.setText(notificationText);
                    notification.setReceiverId(provider.getId());

                    sendNotificationToProvider(notification, new Callback<NotificationDTO>() {
                        @Override
                        public void onResponse(Call<NotificationDTO> call, Response<NotificationDTO> notifResponse) {
                            if (notifResponse.isSuccessful()) {
                                Snackbar.make(v, "Notification sent to provider.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(v, "Failed to send notification.", Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDTO> call, Throwable t) {
                            Snackbar.make(v, "Error sending notification: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Snackbar.make(v, "Failed to get provider info.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Snackbar.make(v, "Network error getting provider: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String createNotificationText(ReactionDTO reaction, String offerName) {
        int rating = reaction.getRating() != null ? reaction.getRating() : 0;
        String stars = new String(new char[rating]).replace("\0", "⭐");
        String comment = reaction.getText() != null && !reaction.getText().isEmpty() ? reaction.getText() : "No comment";

        return "You got a new review for \"" + offerName + "\": Comment: " + comment + " - Rating: " + stars + " (" + rating + "/5)";
    }

}
