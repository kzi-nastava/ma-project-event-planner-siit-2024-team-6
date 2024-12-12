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
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateServiceDialog extends DialogFragment {

    // Declare UI components
    private ImageView closeIcon;
    private EditText eventTypesInput, proposedCategoryInput, serviceNameInput, serviceDescriptionInput,
            serviceSpecificsInput, servicePriceInput, serviceDiscountInput,
            bookingDeadlineInput, cancellationDeadlineInput,
            fixedDurationInput, minDurationInput, maxDurationInput;
    private Spinner serviceCategorySpinner;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private RadioGroup durationRadioGroup, bookingConfirmationRadioGroup;
    private RadioButton fixedDurationRadio, minMaxDurationRadio, autoConfirmationRadio, manualConfirmationRadio;
    private Button addPicturesButton, submitButton;
    private TextView bookingLabel, categoryLabel;
    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;
    private EditServiceDialog.OnOfferUpdatedListener listener;

    public interface OnOfferUpdatedListener {
        void onOfferUpdated();
    }
    public void setOnOfferUpdatedListener(EditServiceDialog.OnOfferUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_edit_service, container, false);

        // Initialize UI components
        initializeViews(view);

        // Initialize photo URL list and adapter
        photoUrls = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        // Set up listeners
        setupListeners();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initializeViews(View view) {
        closeIcon = view.findViewById(R.id.close_icon);
        eventTypesInput = view.findViewById(R.id.event_types_input);
        proposedCategoryInput = view.findViewById(R.id.proposed_category_input);
        serviceNameInput = view.findViewById(R.id.service_name_input);
        serviceDescriptionInput = view.findViewById(R.id.service_description_input);
        serviceSpecificsInput = view.findViewById(R.id.service_specifics_input);
        servicePriceInput = view.findViewById(R.id.service_price_input);
        serviceDiscountInput = view.findViewById(R.id.service_discount_input);
        bookingDeadlineInput = view.findViewById(R.id.booking_deadline_input);
        cancellationDeadlineInput = view.findViewById(R.id.cancellation_deadline_input);
        fixedDurationInput = view.findViewById(R.id.fixed_duration_input);
        minDurationInput = view.findViewById(R.id.min_duration_input);
        maxDurationInput = view.findViewById(R.id.max_duration_input);
        serviceCategorySpinner = view.findViewById(R.id.service_category_spinner);
        visibilityCheckbox = view.findViewById(R.id.visibility_checkbox);
        availabilityCheckbox = view.findViewById(R.id.availability_checkbox);
        durationRadioGroup = view.findViewById(R.id.duration_radio_group);
        bookingConfirmationRadioGroup = view.findViewById(R.id.booking_confirmation_radio_group);
        fixedDurationRadio = view.findViewById(R.id.fixed_duration_radio);
        minMaxDurationRadio = view.findViewById(R.id.min_max_duration_radio);
        autoConfirmationRadio = view.findViewById(R.id.auto);
        manualConfirmationRadio = view.findViewById(R.id.manual);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);
        submitButton = view.findViewById(R.id.submit_button);
        bookingLabel = view.findViewById(R.id.booking_text);
        categoryLabel = view.findViewById(R.id.category_text);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
    }

    private void setupListeners() {
        closeIcon.setOnClickListener(v -> dismiss());

        addPicturesButton.setOnClickListener(v -> addPhotoUrl());

        durationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixed_duration_radio) {
                fixedDurationInput.setVisibility(View.VISIBLE);
                minDurationInput.setVisibility(View.GONE);
                maxDurationInput.setVisibility(View.GONE);
                bookingConfirmationRadioGroup.setVisibility(View.VISIBLE);
                bookingLabel.setVisibility(View.VISIBLE);
            } else {
                fixedDurationInput.setVisibility(View.GONE);
                minDurationInput.setVisibility(View.VISIBLE);
                maxDurationInput.setVisibility(View.VISIBLE);
                bookingConfirmationRadioGroup.setVisibility(View.GONE);
                bookingLabel.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(v -> handleSubmit());
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

    private void handleSubmit() {
        try {
            // Get updated inputs
            String updatedServiceName = serviceNameInput.getText().toString().trim();
            String updatedDescription = serviceDescriptionInput.getText().toString().trim();
            String updatedSpecifics = serviceSpecificsInput.getText().toString().trim();

            // Price and Discount parsing
            double updatedPrice = 0;
            double updatedDiscount = 0;
            if (!servicePriceInput.getText().toString().isEmpty()) {
                updatedPrice = Double.parseDouble(servicePriceInput.getText().toString().trim());
            }
            if (!serviceDiscountInput.getText().toString().isEmpty()) {
                updatedDiscount = Double.parseDouble(serviceDiscountInput.getText().toString().trim());
            }

            // Duration fields
            int updatedFixedDuration = 0;
            int updatedMinDuration = 0;
            int updatedMaxDuration = 0;

            boolean updatedAutoApproval = true;
            if (fixedDurationRadio.isChecked() && !fixedDurationInput.getText().toString().isEmpty()) {
                updatedFixedDuration = Integer.parseInt(fixedDurationInput.getText().toString().trim());
                updatedAutoApproval = autoConfirmationRadio.isChecked();
            } else if (minMaxDurationRadio.isChecked()) {
                updatedAutoApproval = false;
                if (!minDurationInput.getText().toString().isEmpty()) {
                    updatedMinDuration = Integer.parseInt(minDurationInput.getText().toString().trim());
                }
                if (!maxDurationInput.getText().toString().isEmpty()) {
                    updatedMaxDuration = Integer.parseInt(maxDurationInput.getText().toString().trim());
                }
            }

            // Booking and Cancellation deadlines
            int updatedBookingDeadline = 0;
            int updatedCancellationDeadline = 0;
            if (!bookingDeadlineInput.getText().toString().isEmpty()) {
                updatedBookingDeadline = Integer.parseInt(bookingDeadlineInput.getText().toString().trim());
            }
            if (!cancellationDeadlineInput.getText().toString().isEmpty()) {
                updatedCancellationDeadline = Integer.parseInt(cancellationDeadlineInput.getText().toString().trim());
            }

            // Event Types
            String eventTypesString = eventTypesInput.getText().toString().trim();
            ArrayList<String> updatedEventTypes = new ArrayList<>();
            if (!eventTypesString.isEmpty()) {
                String[] types = eventTypesString.split(",");
                for (String type : types) {
                    updatedEventTypes.add(type.trim());
                }
            }

            // Checkboxes
            boolean updatedVisibility = visibilityCheckbox.isChecked();
            boolean updatedAvailability = availabilityCheckbox.isChecked();


            // Validate required fields
            if (photoUrls.isEmpty() || updatedEventTypes.isEmpty() || (updatedFixedDuration == 0 && (updatedMaxDuration == 0 || updatedMinDuration == 0)) || (updatedMinDuration == 0 && updatedMaxDuration == 0 && updatedFixedDuration == 0) || updatedCancellationDeadline <= 0 || updatedBookingDeadline <= 0 || updatedServiceName.isEmpty() || updatedDescription.isEmpty() || updatedSpecifics.isEmpty() || updatedDiscount < 0 || updatedPrice <= 0) {
                Snackbar.make(requireView(), "Please fill in all required fields.", Snackbar.LENGTH_LONG).show();
                return;
            }

            List<EventType> eventTypes = new ArrayList<>();
            for (String name: updatedEventTypes) {
                ClientUtils.eventTypeService.findEventType(name).enqueue(new Callback<EventType>() {
                    @Override
                    public void onResponse(Call<EventType> call, Response<EventType> response) {
                        if (response.isSuccessful()) {
                            EventType eventType = response.body();
                            Log.d("EventType", eventType.getName());
                            eventTypes.add(eventType);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventType> call, Throwable t) {
                        Log.e("Error", t.getMessage());
                    }
                });

            }

            // Create or update the Offer object
            OfferDTO updatedOffer = new OfferDTO();
            updatedOffer.setName(updatedServiceName);
            updatedOffer.setDescription(updatedDescription);
            updatedOffer.setPrice(updatedPrice);
            updatedOffer.setSale(updatedDiscount);
            updatedOffer.setSpecifics(updatedSpecifics);
            updatedOffer.setMinDuration(updatedMinDuration);
            updatedOffer.setMaxDuration(updatedMaxDuration);
            updatedOffer.setPreciseDuration(updatedFixedDuration);
            updatedOffer.setLatestReservation(updatedBookingDeadline);
            updatedOffer.setLatestCancelation(updatedCancellationDeadline);
            updatedOffer.setIsVisible(updatedVisibility);
            updatedOffer.setIsAvailable(updatedAvailability);
            updatedOffer.setReservationAutoApproved(updatedAutoApproval);
            updatedOffer.setPhotos(photoUrls);
            List<EventTypeDTO> list = new ArrayList<>();
            for (EventType eventType : eventTypes) {
                EventTypeDTO eventTypeDTO = new EventTypeDTO(eventType);
                list.add(eventTypeDTO);
            }
            updatedOffer.setEventTypes(list);

            // Simulate saving or updating the data (replace with actual logic)
            Toast.makeText(requireContext(), "Service updated successfully!", Toast.LENGTH_SHORT).show();
            ClientUtils.offerService.createProviderService(1, updatedOffer).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        Log.d("CreateOffer", "Offer created successfully: " + response.body().getName());
                        Toast.makeText(getContext(), "Offer created successfully!", Toast.LENGTH_SHORT).show();
                        // Notify the listener
                        if (listener != null) {
                            listener.onOfferUpdated();
                        }
                        // Dismiss the dialog
                        dismiss();
                    } else {
                        Log.e("CreateOffer", "Failed to create offer: " + response.code());
                        Toast.makeText(getContext(), "Failed to create offer", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Offer> call, Throwable t) {
                    Log.e("CreateOffer", "Error: " + t.getMessage());
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter valid numeric values for price, discount, and durations.", Toast.LENGTH_SHORT).show();
            Log.e("EditServiceDialog", "Error parsing numeric values", e);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            Log.e("EditServiceDialog", "Error in handleSave", e);
        }
    }
}
