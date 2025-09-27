package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Build;
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
import com.example.eventure.dto.CategorySuggestionDTO;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.NewCategoryDTO;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;
import com.example.eventure.model.Status;
import com.google.android.material.snackbar.Snackbar;
import com.example.eventure.dto.EventTypeDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateServiceDialog extends DialogFragment {

    // Declare UI components
    private ImageView closeIcon;
    private EditText eventTypesInput, serviceNameInput, serviceDescriptionInput,
            serviceSpecificsInput, servicePriceInput, serviceDiscountInput,
            bookingDeadlineInput, cancellationDeadlineInput,
            fixedDurationInput, minDurationInput, maxDurationInput;
    private Spinner serviceCategorySpinner;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private RadioGroup durationRadioGroup, bookingConfirmationRadioGroup;
    private RadioButton fixedDurationRadio, minMaxDurationRadio, autoConfirmationRadio, manualConfirmationRadio;
    private Button addPicturesButton, submitButton, proposeCategoryButton;
    private TextView bookingLabel, categoryLabel;
    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;
    private LinearLayout eventTypesContainer;

    private OnOfferCreatedListener listener;

    private List<EventTypeDTO> eventTypes = new ArrayList<>();

    private List<String> categoryList = new ArrayList<>();

    private NewCategoryDTO propsed = null;

    private final Map<String, EventTypeDTO> eventTypeMap = new HashMap<>();

    private void loadCategories() {
        ClientUtils.categoryService.getAllCategoryNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    populateCategorySpinner();
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FetchCategories", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select a category");
        for (String category : categoryList) {
            categoryNames.add(category);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceCategorySpinner.setAdapter(adapter);

        // Set the first item as selected by default
        serviceCategorySpinner.setSelection(0);
    }


    public interface OnOfferCreatedListener {
        void onOfferCreated();
    }

    public void setOnOfferCreatedListener(OnOfferCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_edit_service, container, false);

        initializeViews(view);

        loadEventTypes();
        // Initialize UI components

        // Load and populate categories
        loadCategories();

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
        proposeCategoryButton = view.findViewById(R.id.propose_category_button);
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
        eventTypesContainer = view.findViewById(R.id.event_types_container);
    }

    private void setupListeners() {
        proposeCategoryButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.dialog_propose_category, null);

            EditText nameInput = dialogView.findViewById(R.id.category_name_input);
            EditText descInput = dialogView.findViewById(R.id.category_description_input);

            new AlertDialog.Builder(getContext())
                    .setTitle("Propose New Category")
                    .setView(dialogView)
                    .setPositiveButton("Submit", (dialog, which) -> {
                        String name = nameInput.getText().toString().trim();
                        String desc = descInput.getText().toString().trim();

                        if (name.isEmpty()) {
                            Snackbar.make(requireView(), "Category name is required.", Snackbar.LENGTH_LONG).show();
                        } else {
                            propsed = new NewCategoryDTO(name, desc);
                            proposeCategoryButton.setEnabled(false);
                            Snackbar.make(requireView(), "Proposed: " + name, Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

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
            String updatedCategory = null;

            if(propsed == null){
                if (serviceCategorySpinner.getSelectedItem().equals("Select a category")) {
                    Snackbar.make(requireView(), "Please select or propose a new category", Snackbar.LENGTH_LONG).show();
                    return;
                }
                updatedCategory = serviceCategorySpinner.getSelectedItem().toString();
            }

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

            // Event Types
            List<EventTypeDTO> ets = getSelectedEventTypes();
            Log.d("EVENTY", ets.toString());

            // Checkboxes
            boolean updatedVisibility = visibilityCheckbox.isChecked();
            boolean updatedAvailability = availabilityCheckbox.isChecked();


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
            NewOfferDTO newOffer = new NewOfferDTO();
            newOffer.setCategorySuggestion(propsed);
            newOffer.setCategory(updatedCategory);
            if(propsed != null){
                newOffer.setStatus(Status.PENDING);
            }else{
                newOffer.setStatus(Status.ACCEPTED);
            }
            newOffer.setEventTypes(ets);
            newOffer.setName(updatedServiceName);
            newOffer.setDescription(updatedDescription);
            newOffer.setPrice(updatedPrice);
            newOffer.setSale(updatedDiscount);
            newOffer.setSpecifics(updatedSpecifics);
            newOffer.setMinDuration(updatedMinDuration);
            newOffer.setMaxDuration(updatedMaxDuration);
            newOffer.setPreciseDuration(updatedFixedDuration);
            newOffer.setLatestReservation(updatedBookingDeadline);
            newOffer.setLatestCancelation(updatedCancellationDeadline);
            newOffer.setIsVisible(updatedVisibility);
            newOffer.setIsAvailable(updatedAvailability);
            newOffer.setReservationAutoApproved(updatedAutoApproval);
            newOffer.setPhotos(photoUrls);

            // Simulate saving or updating the data (replace with actual logic)
            submitButton.setEnabled(false);
            ClientUtils.offerService.createProviderService(newOffer).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        Log.d("CreateOffer", "Offer created successfully: " + response.body().getName());
                        // Notify the listener
                        if (listener != null) {
                            listener.onOfferCreated();
                        }
                        // Dismiss the dialog
                        showSnackbar("The service successfully created");
                        dismiss();
                    } else {
                        Log.e("CreateOffer", "Failed to create offer: " + response.code());
                        showSnackbar("Failed to create the service");
                        submitButton.setEnabled(false);
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
    private void loadEventTypes() {
        eventTypesContainer.removeAllViews(); // Clear any existing views
        eventTypes.clear();

        ClientUtils.eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.clear();
                    for (EventTypeDTO eventType : response.body()) {
                        eventTypeMap.put(eventType.getName(), eventType);

                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(eventType.getName());
                        checkBox.setTag(eventType.getName());
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
                selected.add(eventTypeMap.get(eventTypeName));
            }
        }

        return selected;
    }
    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

}
