package com.example.eventure.dialogs;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.NewReservationDTO;
import com.example.eventure.dto.ReservationDTO;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyProductDialog extends DialogFragment{

    private Integer productId;
    // ui
    private Spinner eventSpinner;
    private Button buyButton;
    private ImageView closeIcon;

    // data
    private List<String> events = new ArrayList<>();
    private List<EventDTO> dtos = new ArrayList<>();
    private String selectedEvent;


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
    public BuyProductDialog(Integer productId) {
        this.productId = productId;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_buy_product, container, false);

        buyButton = view.findViewById(R.id.buy_button);
        closeIcon = view.findViewById(R.id.close_icon);
        eventSpinner = view.findViewById(R.id.event_spinner);

        // Fetch events for logged-in organizer
        Call<List<EventDTO>> call = ClientUtils.organizerService.getFutureEventsForOrganizer();
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

        buyButton.setOnClickListener(v -> {
            selectedEvent = eventSpinner.getSelectedItem().toString();
            if (isFormValid()) {
                int eventId = -1;
                for(EventDTO dto: dtos){
                    if(dto.getName().equals(selectedEvent)){
                        eventId = dto.getId();
                    }
                }

                ClientUtils.offerService.buyOffer(this.productId, eventId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view, "Purchase successful", Snackbar.LENGTH_SHORT).show();
                            bookingSuccessful = true;
                            view.postDelayed(() -> {
                                dismiss();
                            }, 1700);
                        } else {
                            String serverMsg = extractServerMessage(response);
                            if (serverMsg != null &&
                                    serverMsg.toLowerCase(Locale.US).contains("not have enough allocated funds")) {
                                Snackbar.make(view, "You do not have enough allocated funds to make this purchase.", Snackbar.LENGTH_LONG).show();
                            } else if (serverMsg != null && !serverMsg.isEmpty()) {
                                Snackbar.make(view, serverMsg, Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, "Failed to purchase. Error code: " + response.code(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
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
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
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
    public boolean isFormValid() {
        Log.d("BookingTag", "selectedEvent: " + selectedEvent);

        if (selectedEvent == null || selectedEvent.equals("Select event")) {
            Toast.makeText(requireContext(), "Please select an event", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private @Nullable String extractServerMessage(Response<?> response) {
        try {
            if (response.errorBody() == null) return null;
            String raw = response.errorBody().string();

            // Try JSON first
            try {
                org.json.JSONObject obj = new org.json.JSONObject(raw);
                if (obj.has("message")) return obj.getString("message");
                if (obj.has("error")) return obj.getString("error");
                if (obj.has("detail")) return obj.getString("detail");
            } catch (org.json.JSONException ignore) {
                // Not JSON? fall through and show raw
            }

            // Fallback: return raw text trimmed (server might return plain text)
            return raw.trim().isEmpty() ? null : raw.trim();
        } catch (java.io.IOException e) {
            return null;
        }
    }

}
