package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;

import java.util.List;

public class BudgetRecommendationsAdapter extends RecyclerView.Adapter<BudgetRecommendationsAdapter.ViewHolder> {

    private final List<String> categories;
    private final OnChooseClickListener listener;

    public interface OnChooseClickListener {
        void onChooseClicked(String category);
    }

    public BudgetRecommendationsAdapter(List<String> categories, OnChooseClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommnedation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryText.setText(category);
        holder.chooseButton.setOnClickListener(v -> listener.onChooseClicked(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        Button chooseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.category_text);
            chooseButton = itemView.findViewById(R.id.choose_button);
        }
    }
}
