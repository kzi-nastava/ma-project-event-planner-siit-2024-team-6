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
import android.widget.Toast;

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
import com.example.eventure.model.Event;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventDialog extends DialogFragment {

    private ImageView closeIcon;
    private EditText eventNameInput, eventDescriptionInput, eventLocationInput, eventDateInput;
    private EditText minParticipantsInput, maxParticipantsInput;
    private CheckBox isPublicCheckbox;
    private RecyclerView photosRecyclerView;
    private Button addPicturesButton, submitButton;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;

    private String name, description, location, date;
    private int minParticipants, maxParticipants;
    private boolean isPublic;
    private int eventId;

    public static EditEventDialog newInstance(Event event) {
        EditEventDialog dialog = new EditEventDialog();
        Bundle args = new Bundle();
        args.putInt("id", event.getId());
        args.putString("name", event.getName());
        args.putString("description", event.getDescription());
        args.putString("location", event.getPlace());
        args.putString("date", event.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        args.putInt("maxParticipants", event.getMaxParticipants());
        args.putBoolean("isPublic", event.getPublic());
        args.putStringArrayList("photos", new ArrayList<>(event.getPhotos()));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt("id");
            name = getArguments().getString("name");
            description = getArguments().getString("description");
            location = getArguments().getString("location");
            date = getArguments().getString("date");
            minParticipants = getArguments().getInt("minParticipants");
            maxParticipants = getArguments().getInt("maxParticipants");
            isPublic = getArguments().getBoolean("isPublic");
            photoUrls = getArguments().getStringArrayList("photos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event, container, false);

        closeIcon = view.findViewById(R.id.close_icon);
        eventNameInput = view.findViewById(R.id.event_name_input);
        eventDescriptionInput = view.findViewById(R.id.event_description_input);
        eventLocationInput = view.findViewById(R.id.event_location_input);
        eventDateInput = view.findViewById(R.id.event_date_input);
        minParticipantsInput = view.findViewById(R.id.event_min_participants_input);
        maxParticipantsInput = view.findViewById(R.id.event_max_participants_input);
        isPublicCheckbox = view.findViewById(R.id.event_is_public_checkbox);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);
        submitButton = view.findViewById(R.id.submit_event_button);

        eventNameInput.setText(name);
        eventDescriptionInput.setText(description);
        eventLocationInput.setText(location);
        eventDateInput.setText(date);
        minParticipantsInput.setText(String.valueOf(minParticipants));
        maxParticipantsInput.setText(String.valueOf(maxParticipants));
        isPublicCheckbox.setChecked(isPublic);

        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        closeIcon.setOnClickListener(v -> dismiss());

        addPicturesButton.setOnClickListener(v -> addPhotoUrl());

        submitButton.setOnClickListener(v -> handleSave());

        return view;
    }
    public interface OnEventUpdatedListener {
        void onEventUpdated();  // метод, который будет вызван после успешного редактирования
    }

    private OnEventUpdatedListener listener;

    public void setOnEventUpdatedListener(OnEventUpdatedListener listener) {
        this.listener = listener;
    }

    private void addPhotoUrl() {
        EditText input = new EditText(getContext());
        input.setHint("Enter Photo URL");

        new AlertDialog.Builder(getContext())
                .setTitle("Add Photo URL")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newPhotoUrl = input.getText().toString().trim();
                    if (!newPhotoUrl.isEmpty()) {
                        photoUrls.add(newPhotoUrl);
                        photoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleSave() {
        String updatedName = eventNameInput.getText().toString().trim();
        String updatedDescription = eventDescriptionInput.getText().toString().trim();
        String updatedLocation = eventLocationInput.getText().toString().trim();
        String updatedDate = eventDateInput.getText().toString().trim();
        boolean updatedIsPublic = isPublicCheckbox.isChecked();

        int updatedMax = Integer.parseInt(maxParticipantsInput.getText().toString().trim());

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedLocation.isEmpty() || updatedDate.isEmpty()) {
            showSnackbar("All fields must be filled");
            return;
        }

        EventDTO dto = new EventDTO();
        dto.setName(updatedName);
        dto.setDescription(updatedDescription);
        dto.setPlace(updatedLocation);
        dto.setDate(LocalDateTime.parse(updatedDate));
        dto.setMaxParticipants(updatedMax);
        dto.setPublic(updatedIsPublic);
        dto.setPhotos(photoUrls);

        submitButton.setEnabled(false);
        ClientUtils.organizerService.updateEvent(eventId, dto).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onEventUpdated();  // обновить список в вызывающем фрагменте
                    }
                    showSnackbar("Event updated successfully");
                    dismiss();
                } else {
                    showSnackbar("Failed to update event");
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                Log.e("EditEventDialog", "Update failed: " + t.getMessage());
                showSnackbar("Update failed: " + t.getMessage());
                submitButton.setEnabled(true);
            }
        });

    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
}