package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.model.BudgetItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddBudgetItemDialog extends DialogFragment {

    public interface OnBudgetItemAddedListener {
        void onBudgetItemAdded(BudgetItem newItem);
    }

    private Spinner categorySpinner;
    private EditText maxPriceEditText;
    private OnBudgetItemAddedListener listener;

    private List<String> categories;

    public static AddBudgetItemDialog newInstance(ArrayList<String> categories) {
        AddBudgetItemDialog dialog = new AddBudgetItemDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("categories", categories);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnBudgetItemAddedListener(OnBudgetItemAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_budget_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categorySpinner = view.findViewById(R.id.category_spinner);
        maxPriceEditText = view.findViewById(R.id.max_price_edittext);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        if (getArguments() != null) {
            categories = getArguments().getStringArrayList("categories");
        } else {
            categories = new ArrayList<>();
        }

        // Set adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            String selectedCategory = (String) categorySpinner.getSelectedItem();
            String maxPriceStr = maxPriceEditText.getText().toString().trim();

            if (selectedCategory == null || selectedCategory.isEmpty()) {
                Snackbar.make(view, "Please select a category", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (maxPriceStr.isEmpty()) {
                Snackbar.make(view, "Please enter max amount", Snackbar.LENGTH_SHORT).show();
                return;
            }

            double maxPrice;
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException e) {
                Snackbar.make(view, "Invalid max price", Snackbar.LENGTH_SHORT).show();
                return;
            }

            BudgetItem newItem = new BudgetItem();
            newItem.setCategory(selectedCategory);
            newItem.setMaxPrice((int) maxPrice);
            newItem.setCurrPrice(0); // initially 0 spent

            if (listener != null) {
                listener.onBudgetItemAdded(newItem);
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT; // Or specify a fixed height if you want
            getDialog().getWindow().setLayout(width, height);
        }
    }

}
