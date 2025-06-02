package com.example.eventure.adapters;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.activities.OfferDetailsActivity;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Offer;

import java.util.ArrayList;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private List<OfferDTO> offerList;

    public OfferAdapter(List<OfferDTO> offerList) {
        this.offerList = offerList;
    }
    public OfferAdapter() {
        this.offerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
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
    public void addOffers(List<OfferDTO> newOffers) {
        int previousSize = offerList.size();
        offerList.addAll(newOffers);
        notifyItemRangeInserted(previousSize, newOffers.size());
    }
    public void clearOffers() {
        this.offerList.clear();
        notifyDataSetChanged();
    }
    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice, productSalePrice, saleTag;
        ImageView productImage, saleEuroIcon;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productSalePrice = itemView.findViewById(R.id.sale_price);
            productImage = itemView.findViewById(R.id.product_image);
            saleTag = itemView.findViewById(R.id.sale_tag);
            saleEuroIcon = itemView.findViewById(R.id.sale_euro_icon);

        }

        public void bind(OfferDTO offer) {
            productTitle.setText(offer.getName());

            if (offer.getSale() > 0) {
                // Display original price with a line through it
                productPrice.setText(String.format("$%s", offer.getPrice()));
                productPrice.setPaintFlags(productPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                // Display sale price
                productSalePrice.setVisibility(View.VISIBLE);
                productSalePrice.setText(String.format("$%s", offer.getSale()));

                // Display sale tag
                saleTag.setVisibility(View.VISIBLE);

                // Show sale icon (if applicable)
                saleEuroIcon.setVisibility(View.VISIBLE);
            } else {
                // Display original price without line-through
                productPrice.setText(String.format("$%s", offer.getPrice()));
                productPrice.setPaintFlags(productPrice.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));

                // Hide sale-related views
                productSalePrice.setVisibility(View.GONE);
                saleTag.setVisibility(View.GONE);
                saleEuroIcon.setVisibility(View.GONE);
            }

            // Set product image
            String photo = offer.getPhotos().get(0);

            Glide.with(productImage.getContext())
                    .load(photo)
                    .placeholder(R.drawable.event2)
                    .error(R.drawable.error_image)
                    .into(productImage);
            //productImage.setImageResource(Offer.getPhotoID());

            itemView.setOnClickListener(v -> {
                Offer o = new Offer(offer);
                Intent intent = new Intent(itemView.getContext(), OfferDetailsActivity.class);
                intent.putExtra("offer", (Parcelable) o); // Make Offer Parcelable
                itemView.getContext().startActivity(intent);
            });
        }

    }
}
