package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventure.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.NewCategoryDTO;
import com.example.eventure.model.Category;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFormDialog extends DialogFragment {
    private EditText nameEditText, descEditText;
    private Button cancelButton, submitButton;

    private Category existingCategory;

    private CategoryFormListener listener;

    public void setCategoryFormListener(CategoryFormListener listener) {
        this.listener = listener;
    }

    public CategoryFormDialog(Category category) {
        this.existingCategory = category;
    }

    public interface CategoryFormListener {
        void onCategoryCreated(Category newCategory);

        void onCategoryUpdated(Category updatedCategory);
    }


    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);

        nameEditText = view.findViewById(R.id.edit_category_name);
        descEditText = view.findViewById(R.id.edit_category_description);
        cancelButton = view.findViewById(R.id.button_cancel);
        submitButton = view.findViewById(R.id.button_submit);

        if (existingCategory != null) {
            nameEditText.setText(existingCategory.getName());
            descEditText.setText(existingCategory.getDescription());
        }

        cancelButton.setOnClickListener(v -> dismiss());

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String desc = descEditText.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Name required");
                return;
            }

            NewCategoryDTO dto = new NewCategoryDTO(name, desc);

            if (existingCategory == null) {
                // Create new category
                ClientUtils.categoryService.createCategory(dto)
                        .enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful()) {
                                    listener.onCategoryCreated(response.body());
                                    dismiss();
                                    Snackbar.make(view, "Successfully created", Snackbar.LENGTH_SHORT).show();
                                }else{
                                    Snackbar.make(view, "Unable to create", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Update existing category
                ClientUtils.categoryService.updateCategory(existingCategory.getId(), dto)
                        .enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful()) {
                                    listener.onCategoryUpdated(response.body());
                                    dismiss();
                                    Snackbar.make(view, "Succesfully updated", Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(view, "Update failed.", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
