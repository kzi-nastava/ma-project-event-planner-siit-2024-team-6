package com.example.eventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.PriceListItem;

import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.ViewHolder> {

    private final List<PriceListItem> priceList;
    private final Context context;

    public PriceListAdapter(Context context, List<PriceListItem> priceList) {
        this.context = context;
        this.priceList = priceList;
    }

    @NonNull
    @Override
    public PriceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_price_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListAdapter.ViewHolder holder, int position) {
        PriceListItem item = priceList.get(position);
        holder.tvName.setText(item.getOfferName());
        holder.tvPrice.setText(String.format("Price: €%.2f", item.getOfferPrice()));
        holder.tvDiscount.setText(item.getOfferDiscountPrice() > 0 ?
                String.format("Discount: €%.2f", item.getOfferDiscountPrice()) : "Discount: None");
        holder.tvType.setText(item.isService() ? "Service" : "Product");

        holder.btnEdit.setOnClickListener(v -> {
            // Handle edit click here
            Toast.makeText(context, "Edit clicked for: " + item.getOfferName(), Toast.LENGTH_SHORT).show();

            // TODO: Open your edit dialog/activity here
        });
    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDiscount, tvType;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOfferName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvType = itemView.findViewById(R.id.tvType);
            btnEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}
