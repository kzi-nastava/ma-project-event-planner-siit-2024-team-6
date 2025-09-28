package com.example.eventure.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.eventure.clients.EventTypeService;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.eventure.clients.ClientUtils;

public class EditServiceDialog extends DialogFragment {

    // Declare UI components
    private ImageView closeIcon;
    private EditText serviceNameInput, serviceDescriptionInput,
            serviceSpecificsInput, servicePriceInput, serviceDiscountInput,
            bookingDeadlineInput, cancellationDeadlineInput,
            fixedDurationInput, minDurationInput, maxDurationInput;
    private Spinner serviceCategorySpinner;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private RadioGroup durationRadioGroup, bookingConfirmationRadioGroup;
    private RadioButton fixedDurationRadio, minMaxDurationRadio, autoConfirmationRadio, manualConfirmationRadio;
    private Button addPicturesButton, saveButton, proposedCetgoryButton;

    // Service data to edit
    private String currentServiceName, currentDescription, currentSpecifics;
    private ArrayList<String> currentEventTypes;
    private Boolean currentVisability, currentAvailability, currentIsAutoReserved;
    private Integer offer, currentMinDuration, currentMaxDuration, currentPreciseDuration, currentLatestCancellation, currentLatestReservation;
    private TextView categoryLabel, bookingLabel, titleLabel;

    private LinearLayout eventTypesContainer;
    private double currentPrice, currentDiscount;

    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;

    private OnOfferUpdatedListener listener;

    private List<String> eventTypes = new ArrayList<>();

    private final Map<String, EventTypeDTO> eventTypeMap = new HashMap<>();
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
        if (o.getSale() == null){
            args.putDouble("discount", 0.0);
        }else{
            args.putDouble("discount", o.getSale());
        }
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
        closeIcon = view.findViewById(R.id.close_icon);;
        serviceNameInput = view.findViewById(R.id.service_name_input);
        proposedCetgoryButton = view.findViewById(R.id.propose_category_button);
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
        eventTypesContainer = view.findViewById(R.id.event_types_container);

        categoryLabel.setVisibility(View.GONE);
        serviceCategorySpinner.setVisibility(View.GONE);
        proposedCetgoryButton.setVisibility(View.GONE);

        loadEventTypes();

        // Set up listeners
        setupListeners();

        // Populate fields with current data
        populateFields();

        // Initialize RecyclerView and Button
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);

        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);



        return view;
    }

    private void populateFields() {
        // Pre-fill fields with current data
        titleLabel.setText("Edit Service");
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
            manualConfirmationRadio.setChecked(true);
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
            if (updatedDiscount > updatedPrice) {
                showSnackbar("Discounted price cannot exceed the original price.");
                serviceDiscountInput.setError("Must be ≤ original price");
                return;
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


            // Checkboxes
            boolean updatedVisibility = visibilityCheckbox.isChecked();
            boolean updatedAvailability = availabilityCheckbox.isChecked();

            List<EventTypeDTO> ets = getSelectedEventTypes();
            // Validate required fields
            if (photoUrls.isEmpty()) {
                showSnackbar("At least one photo is required.");
                return;
            }
            if (ets.isEmpty()) {
                showSnackbar("Please select at least one event type.");
                return;
            }
            if (updatedFixedDuration == 0 && (updatedMinDuration == 0 || updatedMaxDuration == 0)) {
                showSnackbar("Please enter valid durations.");
                return;
            }
            if (updatedBookingDeadline <= 0 || updatedCancellationDeadline <= 0) {
                showSnackbar("Please enter valid deadlines.");
                return;
            }
            if (updatedServiceName.isEmpty() || updatedDescription.isEmpty() || updatedSpecifics.isEmpty()) {
                showSnackbar("Text fields cannot be empty.");
                return;
            }
            if (updatedPrice <= 0) {
                showSnackbar("Price must be greater than 0.");
                return;
            }
            if (updatedDiscount < 0) {
                showSnackbar("Discount cannot be negative.");
                return;
            }
            if (updatedMinDuration > updatedMaxDuration) {
                showSnackbar("Minimum duration cannot be greater than maximum duration.");
                return;
            }


            // Create or update the Offer object
            NewOfferDTO updatedOffer = new NewOfferDTO();
            updatedOffer.setName(updatedServiceName);
            updatedOffer.setEventTypes(ets);
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

            // Simulate saving or updating the data (replace with actual logic)
            saveButton.setEnabled(false);
            ClientUtils.offerService.editProviderService(offer, updatedOffer).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        Log.d("UPDATE_OFFER", "Offer updated successfully: " + response.body().getName());
                        // Notify the listener
                        if (listener != null) {
                            listener.onOfferUpdated();
                        }
                        // Dismiss the dialog
                        showSnackbar("The service successfully updated");
                        dismiss();
                    } else {
                        Log.e("UpdateOffer", "Failed to update offer: " + response.code());
                        showSnackbar("Failed to update the service");
                        saveButton.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<Offer> call, Throwable t) {
                    Log.e("CreateOffer", "Error: " + t.getMessage());
                }
            });

        } catch (NumberFormatException e) {
            Log.e("EditServiceDialog", "Error parsing numeric values", e);
        } catch (Exception e) {
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

    private void loadEventTypes() {
        eventTypesContainer.removeAllViews(); // Clear any existing views
        eventTypes.clear();

        ClientUtils.eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.clear();
                    for (EventTypeDTO eventType : response.body()) {
                        eventTypes.add(eventType.getName());
                        eventTypeMap.put(eventType.getName(), eventType);

                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(eventType.getName());
                        checkBox.setTag(eventType.getName());
                        for (String s: currentEventTypes){
                            if (eventType.getName().equals(s)){
                                checkBox.setChecked(true);
                            }
                        }
                        eventTypesContainer.addView(checkBox);
                    }
                } else {
                    Log.e("EventTypeError", "Failed to load event types");
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                Log.e("EventTypeError", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<EventTypeDTO> getSelectedEventTypes() {
        List<EventTypeDTO> selected = new ArrayList<>();

        for (int i = 0; i < eventTypesContainer.getChildCount(); i++) {
            View view = eventTypesContainer.getChildAt(i);
            if (view instanceof CheckBox && ((CheckBox) view).isChecked()) {
                String eventTypeName = (String) view.getTag();
                EventTypeDTO e = eventTypeMap.get(eventTypeName);
                selected.add(e);
            }
        }

        return selected;
    }
    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

}
