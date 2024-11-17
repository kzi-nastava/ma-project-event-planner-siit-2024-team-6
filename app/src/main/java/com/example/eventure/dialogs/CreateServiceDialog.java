package com.example.eventure.dialogs;

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

public class CreateServiceDialog extends DialogFragment {

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
    private Button addPicturesButton, submitButton;

    private TextView bookingLabel, categoryLabel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate dialog layout
        View view = inflater.inflate(R.layout.dialog_create_edit_service, container, false);

        // Initialize UI components
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
        submitButton = view.findViewById(R.id.submit_button);
        bookingLabel = view.findViewById(R.id.booking_text);
        categoryLabel = view.findViewById(R.id.category_text);

        categoryLabel.setVisibility(View.VISIBLE);
        serviceCategorySpinner.setVisibility(View.VISIBLE);
        proposedCategoryInput.setVisibility(View.VISIBLE);

        // Set up listeners
        setupListeners();

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog fullscreen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        // Submit button
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        // Validate inputs
        String eventTypes = eventTypesInput.getText().toString();
        String proposedCategory = proposedCategoryInput.getText().toString();
        String serviceName = serviceNameInput.getText().toString();
        String serviceDescription = serviceDescriptionInput.getText().toString();

        if (serviceName.isEmpty() || serviceDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle form submission logic here
        Toast.makeText(requireContext(), "Service submitted successfully!", Toast.LENGTH_SHORT).show();

        dismiss();
    }
}
