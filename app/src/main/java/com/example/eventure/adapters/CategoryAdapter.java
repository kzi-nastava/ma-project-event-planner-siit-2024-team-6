package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvCategoryName);
            description = itemView.findViewById(R.id.tvCategoryDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            name.setText(category.getName());
            description.setText(category.getDescription());

            btnEdit.setOnClickListener(v -> listener.onEdit(category));
            btnDelete.setOnClickListener(v -> listener.onDelete(category));
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categoryList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
