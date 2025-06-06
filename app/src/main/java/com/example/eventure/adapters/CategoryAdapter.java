package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.CategoryFormDialog;
import com.example.eventure.model.Category;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    private final FragmentManager fragmentManager;
    private final Context context;

    public interface OnCategoryClickListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(Context context, FragmentManager fragmentManager, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.fragmentManager = fragmentManager;
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
        int pos = position;
        Category category = categoryList.get(pos);
        holder.bind(category, listener);
        holder.btnEdit.setOnClickListener(v -> {
            CategoryFormDialog dialog = new CategoryFormDialog(category);
            dialog.setCategoryFormListener(new CategoryFormDialog.CategoryFormListener() {
                @Override
                public void onCategoryCreated(Category newCategory) {
                    // update list and adapter
                }

                @Override
                public void onCategoryUpdated(Category updatedCategory) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == (updatedCategory.getId())) {
                            categoryList.set(i, updatedCategory);
                            notifyItemChanged(i);
                            break;
                        }
                    }
                }
            });
            dialog.show(fragmentManager, "EditCategory");
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ClientUtils.categoryService.deleteCategory(category.getId())
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            Snackbar.make(holder.itemView, "Category deleted successfully!", Snackbar.LENGTH_SHORT).show(); // ❌ invalid
                                            categoryList.remove(pos);
                                            notifyItemRemoved(pos);
                                        } else {
                                            Snackbar.make(holder.itemView, "Delete failed", Snackbar.LENGTH_SHORT).show(); // ❌ invalid
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Snackbar.make(holder.itemView, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
