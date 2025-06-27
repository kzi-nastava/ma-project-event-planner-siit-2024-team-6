package com.example.eventure.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.NewActivityDTO;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivityDialog extends DialogFragment {

    private int eventId;
    private EditText nameInput, descriptionInput, locationInput;
    private Button btnPickStart, btnPickEnd, btnSubmit;
    private LocalDateTime startTime, endTime;

    public static AddActivityDialog newInstance(int eventId) {
        AddActivityDialog dialog = new AddActivityDialog();
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
        View view = inflater.inflate(R.layout.dialog_add_activity, container, false);

        eventId = getArguments().getInt("eventId");

        nameInput = view.findViewById(R.id.input_name);
        descriptionInput = view.findViewById(R.id.input_description);
        locationInput = view.findViewById(R.id.input_location);
        btnPickStart = view.findViewById(R.id.btn_pick_start);
        btnPickEnd = view.findViewById(R.id.btn_pick_end);
        btnSubmit = view.findViewById(R.id.btn_submit);

        btnPickStart.setOnClickListener(v -> pickDateTime(true));
        btnPickEnd.setOnClickListener(v -> pickDateTime(false));

        btnSubmit.setOnClickListener(v -> submitActivity(view));

        return view;
    }

    private void pickDateTime(boolean isStart) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(requireContext(), (timeView, hour, minute) -> {
                LocalDateTime picked = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute);
                if (isStart) {
                    startTime = picked;
                    btnPickStart.setText(picked.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                } else {
                    endTime = picked;
                    btnPickEnd.setText(picked.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitActivity(View view) {
        String name = nameInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String loc = locationInput.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || loc.isEmpty() || startTime == null || endTime == null) {
            Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_SHORT).show();
            return;
        }

        NewActivityDTO dto = new NewActivityDTO(name, desc, loc, startTime, endTime);
        ClientUtils.organizerService.addActivity(eventId, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view, "Activity added!", Snackbar.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Snackbar.make(view, "Error while saving", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(view, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
