package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.PhotoAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.NewEventDTO;
import com.example.eventure.model.Status;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventDialog extends DialogFragment {

    private EditText nameInput, descriptionInput, locationInput, dateInput;
    private EditText minParticipantsInput, maxParticipantsInput;
    private CheckBox isPublicCheckbox;
    private Button submitButton, addPhotoButton;
    private ImageView closeIcon;
    private RecyclerView photosRecyclerView;
    private List<String> photoUrls;
    private PhotoAdapter photoAdapter;
    private OnEventCreatedListener listener;

    public interface OnEventCreatedListener {
        void onEventCreated();
    }

    public void setOnEventCreatedListener(OnEventCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event, container, false);

        nameInput = view.findViewById(R.id.event_name_input);
        descriptionInput = view.findViewById(R.id.event_description_input);
        locationInput = view.findViewById(R.id.event_location_input);
        dateInput = view.findViewById(R.id.event_date_input);
        minParticipantsInput = view.findViewById(R.id.event_min_participants_input);
        maxParticipantsInput = view.findViewById(R.id.event_max_participants_input);
        isPublicCheckbox = view.findViewById(R.id.event_is_public_checkbox);
        submitButton = view.findViewById(R.id.submit_event_button);
        addPhotoButton = view.findViewById(R.id.add_pictures_button);
        closeIcon = view.findViewById(R.id.close_icon);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);

        photoUrls = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        closeIcon.setOnClickListener(v -> dismiss());

        addPhotoButton.setOnClickListener(v -> addPhotoUrl());

        submitButton.setOnClickListener(v -> handleSubmit());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void addPhotoUrl() {
        EditText input = new EditText(getContext());
        input.setHint("Enter Photo URL");

        new AlertDialog.Builder(getContext())
                .setTitle("Add Photo URL")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        photoUrls.add(url);
                        photoAdapter.notifyDataSetChanged();
                    } else {
                        showSnackbar("URL cannot be empty");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleSubmit() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        boolean isPublic = isPublicCheckbox.isChecked();
        int minParticipants = 0;
        int maxParticipants = 0;

        try {
            if (!minParticipantsInput.getText().toString().trim().isEmpty()) {
                minParticipants = Integer.parseInt(minParticipantsInput.getText().toString().trim());
            }
            if (!maxParticipantsInput.getText().toString().trim().isEmpty()) {
                maxParticipants = Integer.parseInt(maxParticipantsInput.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            showSnackbar("Invalid number format for participants");
            return;
        }

        if (name.isEmpty() || description.isEmpty() || location.isEmpty() || date.isEmpty()) {
            showSnackbar("Please fill in all fields");
            return;
        }

        NewEventDTO newEvent = new NewEventDTO(name, description, location, date);
        newEvent.setPhotos(photoUrls);
        newEvent.setPublic(isPublic);
        newEvent.setParticipants(minParticipants);
        newEvent.setMaxParticipants(maxParticipants);

        submitButton.setEnabled(false);

        ClientUtils.organizerService.createEvent(newEvent).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    if (listener != null) listener.onEventCreated();
                    showSnackbar("Event created successfully");
                    dismiss();
                } else {
                    showSnackbar("Failed to create event");
                    Log.e("CreateEvent", "Response code: " + response.code());
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                showSnackbar("Error: " + t.getMessage());
                Log.e("CreateEvent", "Failure: ", t);
                submitButton.setEnabled(true);
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
}
