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
import com.example.eventure.adapters.OrganizerEventAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EditEventDialog;
import com.example.eventure.dialogs.EditServiceDialog;
import com.example.eventure.model.Event;
import com.example.eventure.model.Offer;
import com.example.eventure.viewmodel.OrganizerEventViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerEvents extends Fragment {

    private RecyclerView recyclerView;
    private OrganizerEventAdapter eventAdapter;
    private OrganizerEventViewModel eventViewModel;
    private TextView emptyView;
    private ProgressBar progressBar;
    private View root;

    public OrganizerEvents() {}

    public static OrganizerEvents newInstance() {
        return new OrganizerEvents();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        recyclerView = root.findViewById(R.id.organizer_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyView = root.findViewById(R.id.organizer_empty_view);
        progressBar = root.findViewById(R.id.progress_bar_org);

        eventAdapter = new OrganizerEventAdapter(event -> {
            EditEventDialog dialog = EditEventDialog.newInstance(event);
            dialog.setOnEventUpdatedListener(() -> eventViewModel.refresh());

            dialog.show(getChildFragmentManager(), "Editeventsd");
            Log.d("OrganizerEvents", "Edit clicked for: " + event.getId());
        }, event -> {
            // TODO: Replace with real delete confirmation
            Log.d("OrganizerEvents", "Delete clicked for: " + event.getId());
            showSnackbar("Event deleted (mock)");
            showDeleteConfirmationDialog(event);
            // You can call eventViewModel.refresh() if you implement deletion
        });

        recyclerView.setAdapter(eventAdapter);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        eventViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OrganizerEventViewModel(10); // Page size = 10
            }
        }).get(OrganizerEventViewModel.class);
//        eventViewModel.refresh();
        eventViewModel.getPagedEvents().observe(getViewLifecycleOwner(), pagedList -> {
            Log.d("OrganizerEvents", "pagedList size = " + (pagedList != null ? pagedList.size() : "null"));

            eventAdapter.submitList(pagedList);
            progressBar.setVisibility(View.GONE);

            if (pagedList == null || pagedList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                showSnackbar("list of events is empty");
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }
    private void showDeleteConfirmationDialog(Event event) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("No", null)
                .show();
    }
    private void deleteEvent(Event event) {
        ClientUtils.organizerService.deleteEvent(event.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eventViewModel.refresh(); // Обновить список после удаления
                    showSnackbar("Event deleted successfully");
                } else {
                    String errorMsg = "Failed to delete event.";
                    if (response.code() == 400) {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            errorMsg = "This event cannot be deleted due to existing constraints.";
                            Log.e("DELETE_EVENT", "Server response: " + errorBody);
                        } catch (IOException e) {
                            errorMsg = "Error reading error body";
                            Log.e("DELETE_EVENT", "Error reading error body", e);
                        }
                    } else {
                        errorMsg = "Failed with code: " + response.code();
                        Log.e("DELETE_EVENT", errorMsg);
                    }
                    showSnackbar(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE_EVENT", "Network failure: " + t.getMessage());
                showSnackbar("Network error: " + t.getMessage());
            }
        });
    }

    public void searchEvents(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (eventViewModel != null) {
            eventViewModel.searchEvents(query);
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(String message) {
        Snackbar.make(this.root, message, Snackbar.LENGTH_LONG).show();
    }
}
