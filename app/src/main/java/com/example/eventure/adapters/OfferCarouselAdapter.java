package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Offer;

import java.util.List;

public class OfferCarouselAdapter extends RecyclerView.Adapter<OfferCarouselAdapter.OfferViewHolder> {

    private List<Offer> offerList;

    public OfferCarouselAdapter(List<Offer> offerList) {
        if (offerList == null) {
            throw new IllegalArgumentException("Offer list cannot be null");
        }
        this.offerList = offerList;
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
        Offer offer = offerList.get(position);
        holder.bind(offer);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {

        private ImageView offerImage;
        private TextView offerTitle;
        private TextView offerDescription;
        private TextView offerPrice;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);

            offerImage = itemView.findViewById(R.id.top_offer_image);
            offerTitle = itemView.findViewById(R.id.top_offer_title);
            offerDescription = itemView.findViewById(R.id.top_offer_description);
        }

        public void bind(Offer offer) {
            //offerImage.setImageResource(offer.getPhotoID());
            offerTitle.setText(offer.getName());
            offerDescription.setText(offer.getDescription());
        }
    }
}

