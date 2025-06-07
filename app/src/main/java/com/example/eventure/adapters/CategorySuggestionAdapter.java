package com.example.eventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.CategorySuggestion;

import java.util.List;
public class CategorySuggestionAdapter extends RecyclerView.Adapter<CategorySuggestionAdapter.SuggestionViewHolder> {

    List<CategorySuggestion> suggestions;
    Context context;

    public interface SuggestionActionListener {
        void onApproveClicked(int suggestionId, int position);
        void onEditClicked(CategorySuggestion suggestion, int position);
    }

    SuggestionActionListener listener;

    public CategorySuggestionAdapter(List<CategorySuggestion> suggestions, Context context, SuggestionActionListener listener) {
        this.suggestions = suggestions;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_suggestion_card, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        CategorySuggestion suggestion = suggestions.get(position);
        holder.name.setText(suggestion.getName());
        holder.description.setText(suggestion.getDescription());
        holder.offerName.setText(suggestion.getOfferName());
        holder.offerDescription.setText(suggestion.getOfferDescription());

        holder.approveButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onApproveClicked(suggestion.getId(), pos);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onEditClicked(suggestion, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, offerName, offerDescription;
        Button approveButton, editButton;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.suggestionCategoryName);
            description = itemView.findViewById(R.id.suggestionCategoryDescription);
            offerName = itemView.findViewById(R.id.suggestionOfferName);
            offerDescription = itemView.findViewById(R.id.suggestionOfferDescription);
            approveButton = itemView.findViewById(R.id.btnApprove);
            editButton = itemView.findViewById(R.id.btnEdit);
        }
    }

    public void removeAt(int position) {
        suggestions.remove(position);
        notifyItemRemoved(position);
    }
}
