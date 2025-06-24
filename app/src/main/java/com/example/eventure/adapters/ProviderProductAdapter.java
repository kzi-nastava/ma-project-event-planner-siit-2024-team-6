package com.example.eventure.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
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

public class ProviderProductAdapter extends PagedListAdapter<Offer, ProviderProductAdapter.ProductViewHolder> {

    private final OnEditClickListener onEditClick;
    private final OnDeleteClickListener onDeleteClick;

    public interface OnEditClickListener {
        void onEdit(Offer offer);
    }

    public interface OnDeleteClickListener {
        void onDelete(Offer offer);
    }

    public ProviderProductAdapter(OnEditClickListener editClick, OnDeleteClickListener deleteClick) {
        super(DIFF_CALLBACK);
        this.onEditClick = editClick;
        this.onDeleteClick = deleteClick;
    }

    private static final DiffUtil.ItemCallback<Offer> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
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
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Offer product = getItem(position);
        if (product == null) return;

        holder.title.setText(product.getName());
        holder.category.setText(product.getCategory());

        // Cena i akcija
        holder.price.setText(String.format("€%.2f", product.getPrice()));
        if (product.getSale() != null && product.getSale() > 0.0) {
            holder.sale.setVisibility(View.VISIBLE);
            holder.sale.setText(String.format("€%.2f", product.getSale()));
            holder.saleTag.setVisibility(View.VISIBLE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.sale.setVisibility(View.GONE);
            holder.saleTag.setVisibility(View.GONE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Slika
        if (product.getPhotos() != null && !product.getPhotos().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getPhotos().get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .timeout(10000)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.placeholder_image);
        }

        // Dugmići
        holder.edit.setOnClickListener(v -> onEditClick.onEdit(product));
        holder.delete.setOnClickListener(v -> onDeleteClick.onDelete(product));
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, price, sale, saleTag;
        ImageView image;
        Button edit, delete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_title);
            category = itemView.findViewById(R.id.product_category);
            price = itemView.findViewById(R.id.provider_product_price);
            sale = itemView.findViewById(R.id.provider_sale_price);
            saleTag = itemView.findViewById(R.id.provider_sale_tag);
            image = itemView.findViewById(R.id.provider_product_image);
            edit = itemView.findViewById(R.id.edit_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }
}
