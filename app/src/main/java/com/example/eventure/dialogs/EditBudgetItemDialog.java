package com.example.eventure.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.model.BudgetItem;

public class EditBudgetItemDialog extends DialogFragment {

    public interface OnBudgetItemUpdatedListener {
        void onBudgetItemUpdated(BudgetItem updatedItem);
    }

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_MAX = "max";
    private static final String ARG_CURRENT = "current";

    private OnBudgetItemUpdatedListener listener;

    public static EditBudgetItemDialog newInstance(BudgetItem item) {
        EditBudgetItemDialog dialog = new EditBudgetItemDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, item.getCategory());
        args.putInt(ARG_MAX, item.getMaxPrice());
        args.putInt(ARG_CURRENT, item.getCurrPrice());
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnBudgetItemUpdatedListener(OnBudgetItemUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_budget_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String category = getArguments().getString(ARG_CATEGORY);
        int maxPrice = getArguments().getInt(ARG_MAX);
        int currPrice = getArguments().getInt(ARG_CURRENT);

        TextView categoryView = view.findViewById(R.id.category_text);
        EditText maxPriceInput = view.findViewById(R.id.edit_max_price);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        categoryView.setText(category);
        maxPriceInput.setText(String.valueOf(maxPrice));
        maxPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            try {
                int newMax = Integer.parseInt(maxPriceInput.getText().toString());
                BudgetItem updated = new BudgetItem();
                updated.setCategory(category);
                updated.setMaxPrice(newMax);
                updated.setCurrPrice(currPrice);
                if (listener != null) listener.onBudgetItemUpdated(updated);
                dismiss();
            } catch (NumberFormatException e) {
                maxPriceInput.setError("Enter a valid number");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
