package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private OnPriceUpdatedListener priceUpdatedListener;

    public List<PriceListItem> getPriceList(){
        return this.priceList;
    }

    public void setOnPriceUpdatedListener(OnPriceUpdatedListener listener) {
        this.priceUpdatedListener = listener;
    }

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
            Context context = v.getContext();
            PriceListItem priceItem = priceList.get(holder.getAdapterPosition());

            // Call the method to show dialog
            showEditPriceDialog(context, priceItem, updatedItem -> {
                // Update the item in your list
                priceList.set(holder.getAdapterPosition(), updatedItem);
                notifyItemChanged(holder.getAdapterPosition());
            });
        });
    }

    public interface OnPriceUpdatedListener {
        void onPriceUpdated(PriceListItem updatedItem);
    }


    private void showEditPriceDialog(Context context, PriceListItem priceItem, OnPriceUpdatedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_price, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView editTitle = dialogView.findViewById(R.id.editPriceTitle);
        EditText editAmount = dialogView.findViewById(R.id.editPriceAmount);
        EditText editDiscount = dialogView.findViewById(R.id.editPriceDiscount);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        editTitle.setText(priceItem.getOfferName());
        editAmount.setText(String.valueOf(priceItem.getOfferPrice()));

        // Optional: Check if discount already exists
        if (priceItem.getOfferDiscountPrice() > 0) {
            editDiscount.setText(String.valueOf(priceItem.getOfferDiscountPrice()));
        }

        btnSubmit.setOnClickListener(v -> {
            String newAmountStr = editAmount.getText().toString().trim();
            String newDiscountStr = editDiscount.getText().toString().trim();

            if (!newAmountStr.isEmpty()) {
                try {
                    double newAmount = Double.parseDouble(newAmountStr);
                    double newDiscount = newDiscountStr.isEmpty() ? 0.0 : Double.parseDouble(newDiscountStr);
                    if (newDiscount > 0 && newDiscount >= newAmount) {
                        editDiscount.setError("Discount must be less than price");
                        return;
                    }

                    priceItem.setOfferPrice(newAmount);
                    Log.e("HEREE", newDiscountStr);
                    priceItem.setOfferDiscountPrice(newDiscount);

                    if (priceUpdatedListener != null) {
                        priceUpdatedListener.onPriceUpdated(priceItem);
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    if (!newAmountStr.matches("\\d+(\\.\\d+)?")) {
                        editAmount.setError("Invalid price");
                    }
                    if (!newDiscountStr.isEmpty() && !newDiscountStr.matches("\\d+(\\.\\d+)?")) {
                        editDiscount.setError("Invalid discount");
                    }
                }
            } else {
                if (newAmountStr.isEmpty()) editAmount.setError("Required");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
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
