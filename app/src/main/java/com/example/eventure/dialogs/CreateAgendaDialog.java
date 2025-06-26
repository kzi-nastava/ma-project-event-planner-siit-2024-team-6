package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ActivityAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ActivityDTO;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAgendaDialog extends DialogFragment {

    private int eventId;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private final List<ActivityDTO> activityList = new ArrayList<>();

    public static CreateAgendaDialog newInstance(int eventId) {
        CreateAgendaDialog dialog = new CreateAgendaDialog();
        Bundle args = new Bundle();
        args.putInt("eventId", eventId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_agenda, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getInt("eventId");
        }

        recyclerView = view.findViewById(R.id.activity_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivityAdapter(activityList, this::deleteActivity);
        recyclerView.setAdapter(adapter);

        loadActivities();

        Button addActivityButton = view.findViewById(R.id.btn_add_activity);
        addActivityButton.setOnClickListener(v -> {
            Snackbar.make(view, "Add activity clicked ", Snackbar.LENGTH_SHORT).show();
            AddActivityDialog.newInstance(eventId).show(getParentFragmentManager(), "add_activity");

        });

        return view;
    }

    private void loadActivities() {
        ClientUtils.organizerService.getActivities(eventId).enqueue(new Callback<List<ActivityDTO>>() {
            @Override
            public void onResponse(Call<List<ActivityDTO>> call, Response<List<ActivityDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activityList.clear();
                    activityList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(requireView(), "Failed to load activities", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActivityDTO>> call, Throwable t) {
                Snackbar.make(requireView(), "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteActivity(int activityId) {
        ClientUtils.organizerService.deleteActivity(eventId, activityId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadActivities(); // reload after delete
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(requireView(), "Failed to delete activity", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
