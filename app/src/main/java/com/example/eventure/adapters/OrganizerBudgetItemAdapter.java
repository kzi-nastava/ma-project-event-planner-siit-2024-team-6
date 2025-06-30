package com.example.eventure.adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.BudgetItem;

import java.util.ArrayList;
import java.util.List;

public class OrganizerBudgetItemAdapter extends RecyclerView.Adapter<OrganizerBudgetItemAdapter.BudgetViewHolder> {

    public interface OnBudgetActionListener {
        void onEdit(BudgetItem item);
        void onDelete(BudgetItem item);
    }

    private List<BudgetItem> budgetItems = new ArrayList<>();
    private OnBudgetActionListener listener;

    public OrganizerBudgetItemAdapter(List<BudgetItem> budgetItems, OnBudgetActionListener listener) {
        this.budgetItems.addAll(budgetItems);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_row, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetItem item = budgetItems.get(position);
        Log.d("BudgetItemAdapter", "Binding item: " + item.getCategory());
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        int count = budgetItems.size();
        Log.d("AdapterDebug", "getItemCount called, count = " + count);
        return count;
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView category, maxPrice, spent;
        ImageButton editBtn, deleteBtn;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            maxPrice = itemView.findViewById(R.id.max_price);
            spent = itemView.findViewById(R.id.spent_price);
            editBtn = itemView.findViewById(R.id.edit_button);
            deleteBtn = itemView.findViewById(R.id.delete_button);
        }

        void bind(BudgetItem item) {
            category.setText(item.getCategory());
            maxPrice.setText("$" + item.getMaxPrice());
            spent.setText("$" + item.getCurrPrice());

            editBtn.setOnClickListener(v -> listener.onEdit(item));
            deleteBtn.setOnClickListener(v -> listener.onDelete(item));
        }
    }
    public void updateItems(List<BudgetItem> newItems) {
        Log.d("AdapterDebug", "Updating with " + newItems.size() + " items");
        budgetItems.clear();
        budgetItems.addAll(newItems);
        notifyDataSetChanged();
    }

}