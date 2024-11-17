package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.PAS;

import java.util.List;

public class PASAdapter extends RecyclerView.Adapter<PASAdapter.PASViewHolder> {

    private List<PAS> pasList;

    public PASAdapter(List<PAS> pasList) {
        this.pasList = pasList;
    }

    @NonNull
    @Override
    public PASViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new PASViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PASViewHolder holder, int position) {
        PAS pas = pasList.get(position);
        holder.bind(pas);
    }

    @Override
    public int getItemCount() {
        return pasList.size();
    }

    static class PASViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice, productSalePrice, saleTag;
        ImageView productImage, saleEuroIcon;

        public PASViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productSalePrice = itemView.findViewById(R.id.sale_price);
            productImage = itemView.findViewById(R.id.product_image);
            saleTag = itemView.findViewById(R.id.sale_tag);
            saleEuroIcon = itemView.findViewById(R.id.sale_euro_icon);
        }

        public void bind(PAS pas) {
            productTitle.setText(pas.getTitle());

            if (pas.getSale() > 0) {
                // Display original price with a line through it
                productPrice.setText(String.format("$%s", pas.getPrice()));
                productPrice.setPaintFlags(productPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                // Display sale price
                productSalePrice.setVisibility(View.VISIBLE);
                productSalePrice.setText(String.format("$%s", pas.getSale()));

                // Display sale tag
                saleTag.setVisibility(View.VISIBLE);

                // Show sale icon (if applicable)
                saleEuroIcon.setVisibility(View.VISIBLE);
            } else {
                // Display original price without line-through
                productPrice.setText(String.format("$%s", pas.getPrice()));
                productPrice.setPaintFlags(productPrice.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));

                // Hide sale-related views
                productSalePrice.setVisibility(View.GONE);
                saleTag.setVisibility(View.GONE);
                saleEuroIcon.setVisibility(View.GONE);
            }

            // Set product image
            productImage.setImageResource(pas.getPhotoID());
        }

    }
}
