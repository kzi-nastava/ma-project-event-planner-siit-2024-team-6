package com.example.eventure.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.NewCategoryDTO;
import com.example.eventure.model.CategorySuggestion;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EditCategorySuggestionDialog extends DialogFragment {

    private Spinner spinner;
    private EditText editTextName, editTextDescription;
    private Button btnSubmit, btnCancel;
    private int suggestionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_category_suggestion, container, false);

        // ✅ Initialize UI elements
        spinner = view.findViewById(R.id.spinner_existing_categories);
        editTextName = view.findViewById(R.id.edit_suggestion_name);
        editTextDescription = view.findViewById(R.id.edit_suggestion_description);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "");
            String description = args.getString("description", "");
            suggestionId = args.getInt("suggestionId",-1);
            editTextName.setText(name);
            editTextDescription.setText(description);
        }

        // ✅ Call API
        loadCategories();

        // Optional: toggle between spinner and manual entry
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                boolean isNew = selected.equalsIgnoreCase("Select an Existing Category");

                editTextName.setEnabled(isNew);
                editTextDescription.setEnabled(isNew);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCancel.setOnClickListener(v -> dismiss());

        btnSubmit.setOnClickListener(v -> {
            String selectedCategory = (String) spinner.getSelectedItem();
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            boolean isNew = selectedCategory.equalsIgnoreCase("Select an Existing Category");

            if (this.suggestionId == -1) {
                Toast.makeText(getContext(), "Missing suggestion ID", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isNew) {
                // 👉 User picked existing category → reject suggestion
                Call<CategorySuggestion> call = ClientUtils.categoryService.rejectSuggestion(suggestionId, selectedCategory);

                call.enqueue(new Callback<CategorySuggestion>() {
                    @Override
                    public void onResponse(Call<CategorySuggestion> call, Response<CategorySuggestion> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(requireView(), "Successfully rejected the suggestion.", Snackbar.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Snackbar.make(requireView(), "Failed to reject the suggestion.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategorySuggestion> call, Throwable t) {
                        Snackbar.make(requireView(), "error: "+t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

            } else {
                // 👉 New name/description must be provided
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                    Snackbar.make(requireView(), "Name and description cannot be empty", Snackbar.LENGTH_LONG).show();
                    return;
                }

                NewCategoryDTO dto = new NewCategoryDTO(name, description); // Make sure you have this DTO class
                Call<CategorySuggestion> call = ClientUtils.categoryService.updateSuggestion(suggestionId, dto);

                call.enqueue(new Callback<CategorySuggestion>() {
                    @Override
                    public void onResponse(Call<CategorySuggestion> call, Response<CategorySuggestion> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(requireView(), "Suggestion updated. Successfully created a new category.", Snackbar.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Snackbar.make(requireView(), "Failed to update the suggestion.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategorySuggestion> call, Throwable t) {
                        Snackbar.make(requireView(), "error: "+t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });


        return view;
    }

    private void loadCategories() {
        Call<List<String>> call = ClientUtils.categoryService.getCategories();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categories = new ArrayList<>();
                    categories.add("Select an Existing Category");
                    categories.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            categories
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
