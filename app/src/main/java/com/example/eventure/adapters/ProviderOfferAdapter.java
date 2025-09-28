package com.example.eventure.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.eventure.R;
import com.example.eventure.model.Offer;

import java.util.List;

public class ProviderOfferAdapter extends PagedListAdapter<Offer, ProviderOfferAdapter.OfferViewHolder> {

    private OnEditButtonClickListener editButtonClickListener;
    private OnDeleteButtonClickListener deleteButtonClickListener;

    public interface OnEditButtonClickListener {
        void onEditButtonClick(Offer offer);
    }

    // Define the interface for the delete button click listener
    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick(Offer offer);
    }

    public ProviderOfferAdapter(OnEditButtonClickListener listener, OnDeleteButtonClickListener listener2) {
        super(DIFF_CALLBACK);
        this.editButtonClickListener = listener;
        this.deleteButtonClickListener = listener2;
    }

    private static final DiffUtil.ItemCallback<Offer> DIFF_CALLBACK = new DiffUtil.ItemCallback<Offer>() {
        @Override
        public boolean areItemsTheSame(@NonNull Offer oldItem, @NonNull Offer newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Offer oldItem, @NonNull Offer newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_service_card, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = getItem(position);
        if (offer == null) {
            return;
        }

        holder.productTitle.setText(offer.getName());
        holder.productCategory.setText(offer.getCategory());
        holder.productPrice.setText(String.format("€%.2f", offer.getPrice()));

        if (offer.getSale() == null || offer.getSale() == 0.0) {
            holder.productSale.setText("");
            holder.productSaleTag.setVisibility(View.GONE);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.productSale.setVisibility(View.GONE);
        } else {
            holder.productSale.setText(String.format("€%.2f", offer.getSale()));
            holder.productSaleTag.setVisibility(View.VISIBLE);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productSale.setVisibility(View.VISIBLE);
        }

        if (offer.getPhotos() != null && !offer.getPhotos().isEmpty()) {
            String imageUrl = offer.getPhotos().get(0);
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

        holder.editButton.setOnClickListener(v -> {
            if (editButtonClickListener != null) {
                editButtonClickListener.onEditButtonClick(offer);
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            if(deleteButtonClickListener != null){
                deleteButtonClickListener.onDeleteButtonClick(offer);
            }
        });
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productCategory, productPrice, productSale, productSaleTag;
        Button editButton, deleteButton;
        ImageView productImage;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            productCategory = itemView.findViewById(R.id.product_category);
            productPrice = itemView.findViewById(R.id.provider_product_price);
            productSale = itemView.findViewById(R.id.provider_sale_price);
            productSaleTag = itemView.findViewById(R.id.provider_sale_tag);
            productImage = itemView.findViewById(R.id.provider_product_image);
        }
    }
}
