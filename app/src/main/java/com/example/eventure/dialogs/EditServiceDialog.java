package com.example.eventure.dialogs;

import android.annotation.SuppressLint;
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
import com.example.eventure.clients.EventTypeService;
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

import com.example.eventure.clients.ClientUtils;

public class EditServiceDialog extends DialogFragment {

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
    private Button addPicturesButton, saveButton;

    // Service data to edit
    private String currentServiceName, currentDescription, currentSpecifics;
    private ArrayList<String> currentEventTypes;
    private Boolean currentVisability, currentAvailability, currentIsAutoReserved;
    private Integer offer, currentMinDuration, currentMaxDuration, currentPreciseDuration, currentLatestCancellation, currentLatestReservation;
    private TextView categoryLabel, bookingLabel, titleLabel;
    private double currentPrice, currentDiscount;

    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;

    private OnOfferUpdatedListener listener;

    public interface OnOfferUpdatedListener {
        void onOfferUpdated();
    }
    public void setOnOfferUpdatedListener(OnOfferUpdatedListener listener) {
        this.listener = listener;
    }

    public EditServiceDialog() {
        // Default constructor
    }

    public static EditServiceDialog newInstance(Offer o) {
        if(o == null){
            Log.d("Error", "OFFER NULL");
        }
        EditServiceDialog dialog = new EditServiceDialog();
        Bundle args = new Bundle();
        args.putString("serviceName", o.getName());
        args.putString("description", o.getDescription());
        args.putDouble("price", o.getPrice());
        args.putDouble("discount", o.getSale());
        args.putString("specifics", o.getSpecifics());
        args.putInt("minDuration", o.getMinDuration());
        args.putInt("maxDuration", o.getMaxDuration());
        args.putInt("preciseDuration", o.getPreciseDuration());
        args.putInt("latestCancellation", o.getLatestCancellation());
        args.putInt("latestReservation", o.getLatestReservation());
        // Handle potential null values
        args.putBoolean("isVisible", o.getIsVisible() != null ? o.getIsVisible() : false);
        args.putBoolean("isAvailable", o.getIsAvailable() != null ? o.getIsAvailable() : false);
        args.putBoolean("isAuto", o.isReservationAutoApproved());

        List<EventType> et = o.getEventTypes();
        ArrayList<String> names = new ArrayList<>();
        for(EventType e: et){
            names.add(e.getName());
        }
        args.putStringArrayList("eventTypes", names);
        ArrayList<String> s = (ArrayList<String>) o.getPhotos();
        args.putStringArrayList("photos", s);
        args.putInt("offer", o.getId());
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve service details from arguments
        if (getArguments() != null) {
            currentServiceName = getArguments().getString("serviceName");
            currentDescription = getArguments().getString("description");
            currentPrice = getArguments().getDouble("price");
            currentDiscount = getArguments().getDouble("discount");
            currentSpecifics = getArguments().getString("specifics");
            currentMinDuration = getArguments().getInt("minDuration");
            currentMaxDuration = getArguments().getInt("maxDuration");
            currentPreciseDuration = getArguments().getInt("preciseDuration");
            currentLatestCancellation = getArguments().getInt("latestCancellation");
            currentLatestReservation = getArguments().getInt("latestReservation");
            currentVisability = getArguments().getBoolean("isVisible");
            currentAvailability = getArguments().getBoolean("isAvailable");
            currentIsAutoReserved = getArguments().getBoolean("isAuto");
            currentEventTypes = getArguments().getStringArrayList("eventTypes");
            photoUrls = getArguments().getStringArrayList("photos");
            offer = getArguments().getInt("offer");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog fullscreen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate dialog layout
        View view = inflater.inflate(R.layout.dialog_create_edit_service, container, false);

        // Initialize views
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
        saveButton = view.findViewById(R.id.submit_button);
        categoryLabel = view.findViewById(R.id.category_text);
        bookingLabel = view.findViewById(R.id.booking_text);
        titleLabel = view.findViewById(R.id.dialog_title);

        categoryLabel.setVisibility(View.GONE);
        serviceCategorySpinner.setVisibility(View.GONE);
        proposedCategoryInput.setVisibility(View.GONE);

        // Populate fields with current data
        populateFields();

        // Initialize RecyclerView and Button
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);

        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        // Set up listeners
        setupListeners();

        return view;
    }

    private void populateFields() {
        // Pre-fill fields with current data
        titleLabel.setText("Edit Service");
        StringBuilder sb = new StringBuilder("");
        for(String eventType: currentEventTypes){
            sb.append(eventType+",");
        }
        sb.deleteCharAt(sb.length()-1);
        eventTypesInput.setText(sb.toString());
        serviceNameInput.setText(currentServiceName);
        serviceDescriptionInput.setText(currentDescription);
        servicePriceInput.setText(String.valueOf(currentPrice));
        serviceDiscountInput.setText(String.valueOf(currentDiscount));
        serviceSpecificsInput.setText(currentSpecifics);
        cancellationDeadlineInput.setText(String.valueOf(currentLatestCancellation));
        bookingDeadlineInput.setText(String.valueOf(currentLatestReservation));
        if(currentPreciseDuration == 0){
            fixedDurationRadio.setChecked(false);
            minMaxDurationRadio.setChecked(true);
            autoConfirmationRadio.setChecked(false);
            manualConfirmationRadio.setChecked(false);
            fixedDurationInput.setText("");
            minDurationInput.setText(String.valueOf(currentMinDuration));
            maxDurationInput.setText(String.valueOf(currentMaxDuration));
        }else{
            fixedDurationRadio.setChecked(true);
            minMaxDurationRadio.setChecked(false);
            fixedDurationInput.setText(String.valueOf(currentPreciseDuration));
            minDurationInput.setText("");
            maxDurationInput.setText("");
            if(currentIsAutoReserved){
                autoConfirmationRadio.setChecked(true);
                manualConfirmationRadio.setChecked(false);
            }else{
                autoConfirmationRadio.setChecked(false);
                manualConfirmationRadio.setChecked(true);
            }
        }
        if(currentAvailability){
            availabilityCheckbox.setChecked(true);
        }else{
            availabilityCheckbox.setChecked(false);
        }
        if(currentVisability){
            visibilityCheckbox.setChecked(true);
        }else{
            visibilityCheckbox.setChecked(false);
        }
    }

    private void setupListeners() {
        // Close dialog
        closeIcon.setOnClickListener(v -> dismiss());

        // Add pictures button
        addPicturesButton.setOnClickListener(v -> {
            // Handle adding pictures
            Toast.makeText(requireContext(), "Add Pictures clicked", Toast.LENGTH_SHORT).show();
        });

        durationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixed_duration_radio) {
                fixedDurationInput.setVisibility(View.VISIBLE);
                minDurationInput.setVisibility(View.GONE);
                maxDurationInput.setVisibility(View.GONE);
                bookingConfirmationRadioGroup.setVisibility(View.VISIBLE);
                bookingLabel.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.min_max_duration_radio) {
                fixedDurationInput.setVisibility(View.GONE);
                minDurationInput.setVisibility(View.VISIBLE);
                maxDurationInput.setVisibility(View.VISIBLE);
                bookingConfirmationRadioGroup.setVisibility(View.GONE);
                bookingLabel.setVisibility(View.GONE);
            }
        });

        // Save button
        saveButton.setOnClickListener(v -> handleSave());
        addPicturesButton.setOnClickListener(v -> addPhotoUrl());
    }

    private void handleSave() {
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
            ClientUtils.offerService.editProviderService(1, offer, updatedOffer).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        Log.d("CreateOffer", "Offer updated successfully: " + response.body().getName());
                        Toast.makeText(getContext(), "Offer updated successfully!", Toast.LENGTH_SHORT).show();
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

    private void addPhotoUrl() {
        // For demonstration purposes, prompt the user to add a URL (you can customize this as needed)
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
}
