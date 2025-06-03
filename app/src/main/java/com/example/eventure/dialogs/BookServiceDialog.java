package com.example.eventure.dialogs;

import android.app.Service;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.NewReservationDTO;
import com.example.eventure.dto.ReservationDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookServiceDialog extends DialogFragment {

    private Integer serviceId;
    private Integer organizerId;
    // ui
    private Spinner eventSpinner;
    private DatePicker datePicker;
    private EditText fromTimeInput, toTimeInput;
    private Button bookButton;
    private ImageView closeIcon;

    // data
    private List<String> events = new ArrayList<>();
    private List<EventDTO> dtos = new ArrayList<>();
    private String selectedEvent;
    private String startDateTime;
    private String endDateTime;


    // booking result
    private boolean bookingSuccessful = false;
    private BookingResultListener bookingResultListener;

    // interface for notifying parent if booking was successful
    public interface BookingResultListener {
        void onBookingResult(boolean wasSuccessful);
    }

    public void setBookingResultListener(BookingResultListener listener) {
        this.bookingResultListener = listener;
    }

    // booking succeeded
    private void markBookingAsSuccessfulAndClose() {
        bookingSuccessful = true;
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (bookingResultListener != null) {
            bookingResultListener.onBookingResult(bookingSuccessful);
        }
    }

    // dialog
    public BookServiceDialog(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.dialog_book_service, container, false);

        // ui
        eventSpinner = view.findViewById(R.id.event_spinner);
        //enable only future dates
        datePicker = view.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());
        fromTimeInput = view.findViewById(R.id.from_time_input);
        toTimeInput = view.findViewById(R.id.to_time_input);
        bookButton = view.findViewById(R.id.book_button);
        closeIcon = view.findViewById(R.id.close_icon);

        // Fetch events for logged-in organizer
        organizerId = ClientUtils.getAuthService().getUserId();
        Call<List<EventDTO>> call = ClientUtils.organizerService.getFutureEventsForOrganizer(organizerId);
        call.enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dtos = response.body();
                    events.add("Select event");
                    for( EventDTO event : dtos){
                        events.add(event.getName());
                    }
                    Log.d("BookingTag","duzina events "+String.valueOf(events.size()));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, events);
                    eventSpinner.setAdapter(adapter);

                } else {
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
            }
        });

        fromTimeInput.setOnClickListener(v -> showTimePicker((hour, minute) -> {
            fromTimeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }));

        toTimeInput.setOnClickListener(v -> showTimePicker((hour, minute) -> {
            toTimeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }));

        bookButton.setOnClickListener(v -> {
            selectedEvent = eventSpinner.getSelectedItem().toString();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String fromTimeStr = fromTimeInput.getText().toString();
            String toTimeStr = toTimeInput.getText().toString();
            startDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02dT%s:00", year, month, day, fromTimeStr);
            endDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02dT%s:00", year, month, day, toTimeStr);

            if (isFormValid(fromTimeStr,toTimeStr)) {
                NewReservationDTO newReservation = new NewReservationDTO();
                newReservation.setServiceId(serviceId);
                newReservation.setEventId(getSelectedEventId());
                newReservation.setStartTime(startDateTime);
                newReservation.setEndTime(endDateTime);

                ClientUtils.reservationService.addReservation(newReservation).enqueue(new Callback<ReservationDTO>() {
                    @Override
                    public void onResponse(Call<ReservationDTO> call, Response<ReservationDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getContext(), "Booking was successful!", Toast.LENGTH_SHORT).show();
                            markBookingAsSuccessfulAndClose();
                        } else {
                            Toast.makeText(getContext(), "Booking for this date and time was unsuccessful. Please choose some other time.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReservationDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        closeIcon.setOnClickListener(v -> dismiss());

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }

    public Integer getSelectedEventId() {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).equals(selectedEvent)) {
                return dtos.get(i-1).getId();
            }
        }
        return -1;
    }
    public boolean isFormValid(String fromTimeStr, String toTimeStr) {
        Log.d("BookingTag", "selectedEvent: " + selectedEvent
                + ", startDateTime: " + startDateTime
                + ", endDateTime: " + endDateTime);

        if (selectedEvent == null || selectedEvent.equals("Select event")) {
            Toast.makeText(requireContext(), "Please select an event", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromTimeStr == null || fromTimeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a valid start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (toTimeStr == null || toTimeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a valid end time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (startDateTime == null) {
            Toast.makeText(requireContext(), "Start date and time are invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endDateTime == null) {
            Toast.makeText(requireContext(), "End date and time are invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void showTimePicker(TimePickedListener listener) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) ->
                listener.onTimePicked(hourOfDay, minute), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private interface TimePickedListener {
        void onTimePicked(int hour, int minute);
    }

}
