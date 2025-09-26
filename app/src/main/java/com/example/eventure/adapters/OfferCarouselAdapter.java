package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.dialogs.EventDetailsDialog;
import com.example.eventure.dialogs.OfferDetailsDialog;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Event;
import com.example.eventure.model.Offer;

import java.util.List;

public class OfferCarouselAdapter extends RecyclerView.Adapter<OfferCarouselAdapter.OfferViewHolder> {

    private List<OfferDTO> offerList;
    private FragmentActivity activity;


    public OfferCarouselAdapter(List<OfferDTO> offerList, FragmentActivity fragmentActivity) {
        if (offerList == null) {
            throw new IllegalArgumentException("Offer list cannot be null");
        }
        this.offerList = offerList;
        this.activity = fragmentActivity;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_offer_card, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        OfferDTO offer = offerList.get(position);
        holder.bind(offer);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {

        private ImageView offerImage;
        private TextView offerTitle;
        private TextView offerDescription;
        private TextView offerPrice;
        private TextView viewButton;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);

            offerImage = itemView.findViewById(R.id.top_offer_image);
            offerTitle = itemView.findViewById(R.id.top_offer_title);
            offerDescription = itemView.findViewById(R.id.top_offer_description);
            viewButton = itemView.findViewById(R.id.top_offer_view_button);

        }

        public void bind(OfferDTO offer) {
            String photo = offer.getPhotos().get(0);

            Glide.with(offerImage.getContext())
                    .load(photo)
                    .placeholder(R.drawable.event2)
                    .error(R.drawable.error_image)
                    .into(offerImage);
            //offerImage.setImageResource(offer.getPhotoID());
            offerTitle.setText(offer.getName());
            offerDescription.setText(offer.getDescription());
            viewButton.setOnClickListener(v -> {
                Offer fullOffer = new Offer(offer);
                OfferDetailsDialog dialog = OfferDetailsDialog.newInstance(fullOffer);
                dialog.show(activity.getSupportFragmentManager(), "OfferDetailsDialog");
            });
        }
    }
}

