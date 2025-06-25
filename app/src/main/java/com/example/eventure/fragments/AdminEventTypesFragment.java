package com.example.eventure.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.AdminEventTypeAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditEventTypeDialog;
import com.example.eventure.model.EventType;
import com.example.eventure.viewmodel.AdminEventTypeViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEventTypesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminEventTypeAdapter adapter;
    private AdminEventTypeViewModel viewModel;
    private TextView emptyView;
    private ProgressBar progressBar;
    private View root;

    public AdminEventTypesFragment() {}

    public static AdminEventTypesFragment newInstance() {
        return new AdminEventTypesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_admin_event_types, container, false);

        recyclerView = root.findViewById(R.id.admin_event_types_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyView = root.findViewById(R.id.admin_empty_view);
        progressBar = root.findViewById(R.id.progress_bar_admin_event_types);

        adapter = new AdminEventTypeAdapter(eventType -> {
            EditEventTypeDialog dialog = EditEventTypeDialog.newInstance(eventType);
            dialog.setOnEventTypeUpdatedListener(() -> viewModel.refresh());

            dialog.show(getChildFragmentManager(), "EditEventTypeDialog");
            Log.d("AdminEventTypes", "Edit clicked for: " + eventType.getId());
        }, eventType -> {
            Log.d("AdminEventTypes", "Delete clicked for: " + eventType.getId());
            showDeleteConfirmationDialog(eventType);
        }, requireActivity());

        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AdminEventTypeViewModel(10);
            }
        }).get(AdminEventTypeViewModel.class);

        viewModel.getPagedEventTypes().observe(getViewLifecycleOwner(), pagedList -> {
            Log.d("AdminEventTypes", "pagedList size = " + (pagedList != null ? pagedList.size() : "null"));

            adapter.submitList(pagedList);
            progressBar.setVisibility(View.GONE);

            if (pagedList == null || pagedList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                showSnackbar("List of event types is empty");
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    private void showDeleteConfirmationDialog(EventType eventType) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event Type")
                .setMessage("Are you sure you want to delete this event type?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEventType(eventType))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEventType(EventType eventType) {
        ClientUtils.adminService.deleteEventType(eventType.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    viewModel.refresh();
                    showSnackbar("Event type deleted successfully");
                } else {
                    String errorMsg = "Failed to delete event type.";
                    if (response.code() == 400) {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            errorMsg = "This event type cannot be deleted due to existing constraints.";
                            Log.e("DELETE_EVENT_TYPE", "Server response: " + errorBody);
                        } catch (IOException e) {
                            errorMsg = "Error reading error body";
                            Log.e("DELETE_EVENT_TYPE", "Error reading error body", e);
                        }
                    } else {
                        errorMsg = "Failed with code: " + response.code();
                        Log.e("DELETE_EVENT_TYPE", errorMsg);
                    }
                    showSnackbar(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE_EVENT_TYPE", "Network failure: " + t.getMessage());
                showSnackbar("Network error: " + t.getMessage());
            }
        });
    }

    public void searchEventTypes(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (viewModel != null) {
            viewModel.searchEventTypes(query);
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(String message) {
        Snackbar.make(this.root, message, Snackbar.LENGTH_LONG).show();
    }
}
