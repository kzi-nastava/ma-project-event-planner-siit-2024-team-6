package com.example.eventure.dialogs;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.eventure.dto.EventTypeDTO;
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
    private Spinner eventTypeSpinner;
    private List<EventTypeDTO> eventTypes = new ArrayList<>();


    //invitations elements
    private RadioGroup visibilityGroup;
    private RadioButton publicRadio, privateRadio;
    private LinearLayout invitationsSection;
    private EditText invitationEmailInput;
    private Button addInvitationButton;
    private TextView invitationsListText;
    private List<String> invitedEmails = new ArrayList<>();


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
        dateInput = view.findViewById(R.id.event_date_input); // 🛠️ ДОБАВЛЕНО: найти поле
        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        dateInput.setOnClickListener(v -> openDateTimePicker());

        maxParticipantsInput = view.findViewById(R.id.event_max_participants_input);
        isPublicCheckbox = view.findViewById(R.id.event_is_public_checkbox);
        isPublicCheckbox.setVisibility(View.GONE);
        submitButton = view.findViewById(R.id.submit_event_button);
        addPhotoButton = view.findViewById(R.id.add_pictures_button);
        closeIcon = view.findViewById(R.id.close_icon);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);

        photoUrls = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);
        eventTypeSpinner = view.findViewById(R.id.event_type_spinner);

        visibilityGroup = view.findViewById(R.id.event_visibility_group);
        visibilityGroup.setVisibility(View.VISIBLE);
        publicRadio = view.findViewById(R.id.event_public_radio);
        privateRadio = view.findViewById(R.id.event_private_radio);
        invitationsSection = view.findViewById(R.id.invitations_section);
        invitationEmailInput = view.findViewById(R.id.invitation_email_input);
        addInvitationButton = view.findViewById(R.id.add_invitation_button);
        invitationsListText = view.findViewById(R.id.invitations_list_text);

        visibilityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.event_private_radio) {
                invitationsSection.setVisibility(View.VISIBLE);
            } else {
                invitationsSection.setVisibility(View.GONE);
                invitedEmails.clear();
                invitationsListText.setText("");
            }
        });

        addInvitationButton.setOnClickListener(v -> {
            String email = invitationEmailInput.getText().toString().trim();
            if (email.isEmpty()) {
                showSnackbar("Email cannot be empty");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showSnackbar("Invalid email format");
                return;
            }
            if (invitedEmails.contains(email)) {
                showSnackbar("This email is already invited");
                return;
            }

            invitedEmails.add(email);
            invitationsListText.setText(String.join("\n", invitedEmails));
            invitationEmailInput.setText("");
        });

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
                } else {
                    Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        closeIcon.setOnClickListener(v -> dismiss());
        addPhotoButton.setOnClickListener(v -> addPhotoUrl());
        submitButton.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void openDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (timeView, hourOfDay, minute) -> {
                                LocalDateTime selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute);
                                String formatted = selectedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                dateInput.setText(formatted);
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
        String dateStr = dateInput.getText().toString().trim();
        if (dateStr.isEmpty()) {
            showSnackbar("Please enter date and time");
            return;
        }
        LocalDateTime date;
        try {
            date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            showSnackbar("Invalid date format. Use e.g. 2025-07-15T18:00");
            return;
        }

        boolean isPublic = publicRadio.isChecked();
        int minParticipants = 0;
        int maxParticipants = 0;

        try {
            if (!maxParticipantsInput.getText().toString().trim().isEmpty()) {
                maxParticipants = Integer.parseInt(maxParticipantsInput.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            showSnackbar("Invalid number format for participants");
            return;
        }

        if (name.isEmpty() || description.isEmpty() || location.isEmpty() || date == null) {
            showSnackbar("Please fill in all fields");
            return;
        }

        NewEventDTO newEvent = new NewEventDTO(name, description, location, date);
        newEvent.setPhotos(photoUrls);
        newEvent.setPublic(isPublic);
        if (!isPublic) {
            newEvent.setEmails(invitedEmails);
        }
        newEvent.setParticipants(minParticipants);
        newEvent.setMaxParticipants(maxParticipants);

        submitButton.setEnabled(false);
        EventTypeDTO selectedType = (EventTypeDTO) eventTypeSpinner.getSelectedItem();
        if (selectedType == null) {
            Toast.makeText(getContext(), "Please select event type", Toast.LENGTH_SHORT).show();
            return;
        }
        newEvent.setEventType(selectedType.getName());  // или setEventTypeName()

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
