package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.BudgetRecommendationsAdapter;
import com.example.eventure.model.BudgetItem;

import java.util.ArrayList;
import java.util.List;

public class BudgetRecommendationsDialog extends DialogFragment {

    public interface OnCategoryChosenListener {
        void onCategoryChosen(BudgetItem item);
    }

    private OnCategoryChosenListener listener;
    private List<String> recommendedCategories;

    public void setOnCategoryChosenListener(OnCategoryChosenListener listener) {
        this.listener = listener;
    }

    public static BudgetRecommendationsDialog newInstance(List<String> recommendedCategories) {
        BudgetRecommendationsDialog dialog = new BudgetRecommendationsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("categories", (ArrayList<String>) recommendedCategories);

        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recommendedCategories = getArguments() != null ? getArguments().getStringArrayList("categories") : new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recommendations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BudgetRecommendationsAdapter adapter = new BudgetRecommendationsAdapter(recommendedCategories, category -> {
            promptForMaxPrice(category);
        });
        view.findViewById(R.id.close_button).setOnClickListener(v -> dismiss());
        recyclerView.setAdapter(adapter);
    }
    private void promptForMaxPrice(String category) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Enter max amount for " + category);

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Max amount");

        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                int max = Integer.parseInt(value);
                BudgetItem item = new BudgetItem();
                item.setCategory(category);
                item.setMaxPrice(max);
                item.setCurrPrice(0);

                if (listener != null) {
                    listener.onCategoryChosen(item);
                }
                dismiss();
            } else {
                com.google.android.material.snackbar.Snackbar.make(requireView(), "Amount required", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
