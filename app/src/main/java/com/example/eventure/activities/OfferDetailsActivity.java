package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;

public class OfferDetailsActivity extends AppCompatActivity {

    private Offer offer;
    private ImageButton btnFavorite;
    private Button btnBook;
    private boolean isFavorited;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.offer_details);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        offer = (Offer) getIntent().getParcelableExtra("offer");
        if (offer != null){
            populateUI();
        }else{
            finish();
        }

        ImageButton btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> finish());

        btnFavorite = findViewById(R.id.btn_favorite);
        btnFavorite.setOnClickListener(v -> {
            isFavorited = !isFavorited;
            if (isFavorited){
                btnFavorite.setImageResource(R.drawable.heart_filled_icon);
            }else{
                btnFavorite.setImageResource(R.drawable.heart_icon);
            }// ADD implementation of endpoint request
        });

        btnBook = findViewById(R.id.btn_book);
        btnBook.setOnClickListener(v -> {
            if(offer.getType().equals("Service")){
                //DUSICA
                //Ako prodje uspesno booking, pozovi makeReviewUIVisible, da moze da ostavi review
            }else{

            }
        });

    }

    private void populateProductUI(){
        TextView durationLabel = findViewById(R.id.duration_label);
        durationLabel.setVisibility(View.GONE);
        TextView duration = findViewById(R.id.duration_text);
        duration.setVisibility(View.GONE);
        TextView deadline = findViewById(R.id.deadline_text);
        deadline.setVisibility(View.GONE);
        btnBook = findViewById(R.id.btn_book);
        btnBook.setText("BUY");
    }

    @SuppressLint("ResourceAsColor")
    private void populateUI(){
        TextView title = findViewById(R.id.offer_title);
        title.setText(offer.getName());

        TextView description = findViewById(R.id.details_text);
        String text = offer.getDescription();
        if (offer.getSpecifics() != null){
            text += "\n"+offer.getSpecifics();
        }
        description.setText(text);

        TextView eventTypes = findViewById(R.id.event_types_text);
        StringBuilder sb = new StringBuilder();
        for (EventType et: offer.getEventTypes()){
            sb.append(et.getName()+"\n");
        }
        sb.deleteCharAt(sb.length()-1);
        eventTypes.setText(sb.toString());

        TextView category = findViewById(R.id.category_text);
        category.setText(offer.getCategory());

        TextView priceText = findViewById(R.id.price_text);
        String price = offer.getPrice().toString() + "$";
        String sale = "";
        if (offer.getSale() != null && offer.getSale() > 0.0){
            sale = "  " + offer.getSale() + "$";
        }
        if(sale.equals("")){
            priceText.setText(price);
        }else{
            String end = price+sale;
            SpannableString spannable = new SpannableString(end);
            int startColor = end.indexOf(sale);
            spannable.setSpan(new ForegroundColorSpan(R.color.pink), startColor, startColor + sale.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int strikeStart = end.indexOf(price);
            spannable.setSpan(new StrikethroughSpan(), strikeStart, strikeStart+price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceText.setText(spannable);
        }

        TextView available = findViewById(R.id.availability_text);
        if (offer.getIsAvailable()){
            available.setText("Available.");
        }else{
            available.setText("Currently unavailable.");
            btnBook.setActivated(false);
        }

        ViewPager2 imageCarousel = findViewById(R.id.image_carousel);

        if (offer.getPhotos() != null && !offer.getPhotos().isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(this, offer.getPhotos());
            imageCarousel.setAdapter(adapter);
        }

        if(offer.getType().equals("Service")){
            populateServiceUI();
        }else{
            populateProductUI();
        }

    }

    private void populateServiceUI(){
        TextView duration = findViewById(R.id.duration_text);
        if (offer.getPreciseDuration() == 0){
            duration.setText(offer.getMinDuration()+" - "+offer.getMaxDuration()+" minutes");
        }else{
            duration.setText(offer.getPreciseDuration() + " minutes");
        }

        TextView cancelDeadline = findViewById(R.id.deadline_text);
        cancelDeadline.setText("Cancelation available up until "+offer.getLatestCancellation()+"h before the booking.");
    }

    private void makeReviewUIVisible(){
        TextView reviewLabel = findViewById(R.id.review_label);
        reviewLabel.setVisibility(View.VISIBLE);
        EditText rating = findViewById(R.id.review_rating);
        rating.setVisibility(View.VISIBLE);
        EditText review = findViewById(R.id.review_input);
        review.setVisibility(View.VISIBLE);
        Button submit = findViewById(R.id.btn_submit_review);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(v->{


        });
    }

}
