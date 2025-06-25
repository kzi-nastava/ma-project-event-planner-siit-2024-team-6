package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventTypeDTO;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventTypeDialog extends DialogFragment {

    private EditText nameInput, descriptionInput;
    private Button submitButton;
    private ImageView closeIcon;
    private OnEventTypeCreatedListener listener;

    public interface OnEventTypeCreatedListener {
        void onEventTypeCreated();
    }

    public void setOnEventTypeCreatedListener(OnEventTypeCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event_type, container, false);

        nameInput = view.findViewById(R.id.event_type_name_input);
        descriptionInput = view.findViewById(R.id.event_type_description_input);
        submitButton = view.findViewById(R.id.submit_event_type_button);
        closeIcon = view.findViewById(R.id.close_icon);

        closeIcon.setOnClickListener(v -> dismiss());

        submitButton.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            showSnackbar("Please fill in all fields");
            return;
        }

        EventTypeDTO newEventType = new EventTypeDTO();
        newEventType.setName(name);
        newEventType.setDescription(description);
        newEventType.setDeleted(false);

        submitButton.setEnabled(false);

        ClientUtils.adminService.createEventType(newEventType).enqueue(new Callback<EventTypeDTO>() {
            @Override
            public void onResponse(Call<EventTypeDTO> call, Response<EventTypeDTO> response) {
                if (response.isSuccessful()) {
                    showSnackbar("Event type created");
                    if (listener != null) listener.onEventTypeCreated();
                    dismiss();
                } else {
                    showSnackbar("Creation failed");
                    Log.e("CreateEventType", "Response code: " + response.code());
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<EventTypeDTO> call, Throwable t) {
                showSnackbar("Error: " + t.getMessage());
                Log.e("CreateEventType", "Failure: ", t);
                submitButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
}
