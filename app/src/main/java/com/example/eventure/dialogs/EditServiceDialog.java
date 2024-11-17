package com.example.eventure.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.example.eventure.R;

public class EditServiceDialog extends DialogFragment {

    // Declare UI components
    private ImageView closeIcon;
    private EditText eventTypesInput, proposedCategoryInput, serviceNameInput, serviceDescriptionInput,
            serviceSpecificsInput, servicePriceInput, serviceDiscountInput,
            bookingDeadlineInput, cancellationDeadlineInput,
            fixedDurationInput, minMaxDurationInput;
    private Spinner serviceCategorySpinner;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private RadioGroup durationRadioGroup, bookingConfirmationRadioGroup;
    private RadioButton fixedDurationRadio, minMaxDurationRadio, autoConfirmationRadio, manualConfirmationRadio;
    private Button addPicturesButton, saveButton;

    // Service data to edit
    private String currentServiceName, currentDescription;
    private TextView categoryLabel, bookingLabel;
    private double currentPrice, currentDiscount;

    public EditServiceDialog() {
        // Default constructor
    }

    public static EditServiceDialog newInstance(String serviceName, String description, double price, double discount) {
        EditServiceDialog dialog = new EditServiceDialog();
        Bundle args = new Bundle();
        args.putString("serviceName", serviceName);
        args.putString("description", description);
        args.putDouble("price", price);
        args.putDouble("discount", discount);
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
        minMaxDurationInput = view.findViewById(R.id.min_max_duration_input);
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

        categoryLabel.setVisibility(View.GONE);
        serviceCategorySpinner.setVisibility(View.GONE);
        proposedCategoryInput.setVisibility(View.GONE);

        // Populate fields with current data
        populateFields();

        // Set up listeners
        setupListeners();

        return view;
    }

    private void populateFields() {
        // Pre-fill fields with current data
        serviceNameInput.setText(currentServiceName);
        serviceDescriptionInput.setText(currentDescription);
        servicePriceInput.setText(String.valueOf(currentPrice));
        serviceDiscountInput.setText(String.valueOf(currentDiscount));
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
                minMaxDurationInput.setVisibility(View.GONE);
                bookingConfirmationRadioGroup.setVisibility(View.VISIBLE);
                bookingLabel.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.min_max_duration_radio) {
                fixedDurationInput.setVisibility(View.GONE);
                minMaxDurationInput.setVisibility(View.VISIBLE);
                bookingConfirmationRadioGroup.setVisibility(View.GONE);
                bookingLabel.setVisibility(View.GONE);
            }
        });

        // Save button
        saveButton.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        // Get updated inputs
        String updatedServiceName = serviceNameInput.getText().toString();
        String updatedDescription = serviceDescriptionInput.getText().toString();
        double updatedPrice = Double.parseDouble(servicePriceInput.getText().toString());
        double updatedDiscount = Double.parseDouble(serviceDiscountInput.getText().toString());

        // Validate inputs
        if (updatedServiceName.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle saving logic (e.g., update the database or notify parent activity/fragment)
        Toast.makeText(requireContext(), "Service updated successfully!", Toast.LENGTH_SHORT).show();

        dismiss();
    }
}
