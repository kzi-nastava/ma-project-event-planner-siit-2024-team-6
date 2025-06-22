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
// импортируй эти
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.example.eventure.dto.EventTypeDTO;
import java.util.Calendar;

public class EditEventDialog extends DialogFragment {

    private ImageView closeIcon;
    private EditText eventNameInput, eventDescriptionInput, eventLocationInput, eventDateInput;
    private EditText maxParticipantsInput;
    private CheckBox isPublicCheckbox;
    private RecyclerView photosRecyclerView;
    private Button addPicturesButton, submitButton;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;
    private Spinner eventTypeSpinner;
    private List<EventTypeDTO> eventTypes = new ArrayList<>();

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
        args.putString("date", event.getDate().toString());
        args.putInt("maxParticipants", event.getMaxParticipants());
        args.putBoolean("isPublic", event.getPublic());
        args.putString("eventType", event.getEventType().toString());
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
            maxParticipants = getArguments().getInt("maxParticipants");
            isPublic = getArguments().getBoolean("isPublic");
            photoUrls = getArguments().getStringArrayList("photos");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.9),  // 90% ширины экрана
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
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
        maxParticipantsInput = view.findViewById(R.id.event_max_participants_input);
        isPublicCheckbox = view.findViewById(R.id.event_is_public_checkbox);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);
        submitButton = view.findViewById(R.id.submit_event_button);
        eventTypeSpinner = view.findViewById(R.id.event_type_spinner);

        eventNameInput.setText(name);
        eventDescriptionInput.setText(description);
        eventLocationInput.setText(location);
        eventDateInput.setText(date);
        eventDateInput.setFocusable(false);
        eventDateInput.setClickable(true);
        eventDateInput.setOnClickListener(v -> openDateTimePicker());

        maxParticipantsInput.setText(String.valueOf(maxParticipants));
        isPublicCheckbox.setChecked(isPublic);

        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);
        ClientUtils.eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes = response.body();
                    ArrayAdapter<EventTypeDTO> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            eventTypes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventTypeSpinner.setAdapter(adapter);

                    // Выбрать текущий тип события (по имени)
                    for (int i = 0; i < eventTypes.size(); i++) {
                        if (eventTypes.get(i).getName().equals(getArguments().getString("eventType"))) {
                            eventTypeSpinner.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading event types", Toast.LENGTH_SHORT).show();
            }
        });

        closeIcon.setOnClickListener(v -> dismiss());

        addPicturesButton.setOnClickListener(v -> addPhotoUrl());

        submitButton.setOnClickListener(v -> handleSave());

        return view;
    }
    public interface OnEventUpdatedListener {
        void onEventUpdated();  // метод, который будет вызван после успешного редактирования
    }
    private void openDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (timeView, hourOfDay, minute) -> {
                                LocalDateTime selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute);
                                String formatted = selectedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                eventDateInput.setText(formatted);
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
        try {
            dto.setDate(LocalDateTime.parse(updatedDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception e) {
            showSnackbar("Invalid date format");
            return;
        }
        dto.setMaxParticipants(updatedMax);
        dto.setPublic(updatedIsPublic);
        dto.setPhotos(photoUrls);
        EventTypeDTO selectedType = (EventTypeDTO) eventTypeSpinner.getSelectedItem();
        if (selectedType == null) {
            Toast.makeText(getContext(), "Please select event type", Toast.LENGTH_SHORT).show();
            return;
        }
        dto.setEventType(selectedType); // или .setEventTypeName()

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