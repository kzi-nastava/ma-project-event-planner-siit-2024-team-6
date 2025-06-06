package com.example.eventure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.CategorySuggestionAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditCategorySuggestionDialog;
import com.example.eventure.model.CategorySuggestion;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategorySuggestionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategorySuggestionAdapter adapter;
    private List<CategorySuggestion> categorySuggestions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category_suggestions, container, false);

        recyclerView = view.findViewById(R.id.categorySuggestionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getPagedSuggestions(view); // This will fetch and assign categorySuggestions

        return view;
    }

    private void setupAdapter(View view) {
        adapter = new CategorySuggestionAdapter(categorySuggestions, getContext(), new CategorySuggestionAdapter.SuggestionActionListener() {
            @Override
            public void onApproveClicked(int suggestionId, int position) {
                ClientUtils.categoryService.approveSuggestion(suggestionId).enqueue(new Callback<CategorySuggestion>() {
                    @Override
                    public void onResponse(Call<CategorySuggestion> call, Response<CategorySuggestion> response) {
                        if (response.isSuccessful()) {
                            categorySuggestions.remove(position);
                            adapter.notifyItemRemoved(position);
                            Snackbar.make(view, "Successfully approved the suggestion.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, "Failed to approve the suggestion.", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategorySuggestion> call, Throwable t) {
                        Snackbar.make(view, "An unexpected error occurred.", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEditClicked(CategorySuggestion suggestion, int position) {
                EditCategorySuggestionDialog dialog = new EditCategorySuggestionDialog();
                Bundle args = new Bundle();
                args.putString("name", suggestion.getName());
                args.putString("description", suggestion.getDescription());
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "EditSuggestionDialog");

            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void getPagedSuggestions(View view) {
        Call<PagedResponse<CategorySuggestion>> call = ClientUtils.categoryService.getCategorySuggestions(0, 20);
        call.enqueue(new Callback<PagedResponse<CategorySuggestion>>() {
            @Override
            public void onResponse(Call<PagedResponse<CategorySuggestion>> call, Response<PagedResponse<CategorySuggestion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorySuggestions = response.body().getContent();
                    setupAdapter(view); // Adapter is set only after data is fetched
                } else {
                    Snackbar.make(view, "Failed to get suggestions", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<CategorySuggestion>> call, Throwable t) {
                Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
