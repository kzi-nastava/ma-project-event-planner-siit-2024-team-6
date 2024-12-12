package com.example.eventure.adapters;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.eventure.R;
import com.example.eventure.model.Offer;

import java.util.List;

public class ProviderOfferAdapter extends RecyclerView.Adapter<ProviderOfferAdapter.OfferViewHolder> {

    private List<Offer> offerList;
    private OnEditButtonClickListener editButtonClickListener;

    // Define the interface for the edit button click listener
    public interface OnEditButtonClickListener {
        void onEditButtonClick(Offer offer);
    }

    // Constructor
    public ProviderOfferAdapter(List<Offer> offerList, OnEditButtonClickListener listener) {
        this.offerList = offerList;
        this.editButtonClickListener = listener;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_service_card, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.productTitle.setText(offer.getName());
        holder.productCategory.setText(offer.getCategory());
        holder.productPrice.setText(String.format("€%.2f", offer.getPrice()));
        if(offer.getSale() == null){
            holder.productSale.setText("");
            holder.productSaleTag.setVisibility(View.GONE);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.productSale.setVisibility(View.GONE);
        }else{
            holder.productSale.setText(String.format("€%.2f", offer.getSale()));
            holder.productSaleTag.setVisibility(View.VISIBLE);
            // Cross out the original price
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productSale.setVisibility(View.VISIBLE);
        }
        if (offer.getPhotos() != null && !offer.getPhotos().isEmpty()) {
            String imageUrl = offer.getPhotos().get(0);
            Log.d("GlideImageURL", "Loading image URL: " + imageUrl);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .timeout(10000)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listener for the edit button
        holder.editButton.setOnClickListener(v -> {
            if (editButtonClickListener != null) {
                editButtonClickListener.onEditButtonClick(offer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productCategory, productPrice, productSale, productSaleTag;
        Button editButton;
        ImageView productImage;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            editButton = itemView.findViewById(R.id.edit_button);
            productCategory = itemView.findViewById(R.id.product_category);
            productPrice = itemView.findViewById(R.id.provider_product_price);
            productSale = itemView.findViewById(R.id.provider_sale_price);
            productSaleTag = itemView.findViewById(R.id.provider_sale_tag);
            productImage = itemView.findViewById(R.id.provider_product_image);
        }
    }
}
