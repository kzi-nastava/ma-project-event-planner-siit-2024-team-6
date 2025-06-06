package com.example.eventure.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.CategoryAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.CategoryFormDialog;
import com.example.eventure.model.Category;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminCategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public AdminCategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(requireContext(), getParentFragmentManager(), categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onEdit(Category category) {
                Toast.makeText(getContext(), "Edit: " + category.getName(), Toast.LENGTH_SHORT).show();
                openCategoryFormDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                Toast.makeText(getContext(), "Delete: " + category.getName(), Toast.LENGTH_SHORT).show();
                // TODO: confirm & delete
            }
        });
        recyclerView.setAdapter(adapter);

        setupScrollListener();
        loadCategories();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            openCategoryFormDialog(null);
        });

    }

    private void loadCategories() {
        if (isLoading || isLastPage) return;
        isLoading = true;

        ClientUtils.categoryService.getPagedCategories(currentPage, pageSize).enqueue(new Callback<PagedResponse<Category>>() {
            @Override
            public void onResponse(@NonNull Call<PagedResponse<Category>> call,
                                   @NonNull retrofit2.Response<PagedResponse<Category>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> newCategories = response.body().getContent();
                    categoryList.addAll(newCategories);
                    adapter.notifyDataSetChanged();

                    currentPage++;
                    isLastPage = currentPage >= response.body().getTotalPages();
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagedResponse<Category>> call,
                                  @NonNull Throwable t) {
                isLoading = false;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItem >= totalItemCount) &&
                        firstVisibleItem >= 0) {
                    loadCategories();
                }
            }
        });
    }
    private void openCategoryFormDialog(@Nullable Category category) {
        CategoryFormDialog dialog = new CategoryFormDialog(category);
        dialog.setCategoryFormListener(new CategoryFormDialog.CategoryFormListener() {
            @Override
            public void onCategoryCreated(Category newCategory) {
                categoryList.add(newCategory);
                adapter.notifyItemInserted(categoryList.size() - 1);
                Snackbar.make(requireView(), "Category created", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCategoryUpdated(Category updatedCategory) {
                int index = -1;
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId() == updatedCategory.getId()) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    categoryList.set(index, updatedCategory);
                    adapter.notifyItemChanged(index);
                    Snackbar.make(requireView(), "Category updated", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show(getParentFragmentManager(), "CategoryFormDialog");
    }

}
