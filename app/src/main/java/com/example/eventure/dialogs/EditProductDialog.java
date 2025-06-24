package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.PhotoAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;
import com.google.android.material.snackbar.Snackbar;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductDialog extends DialogFragment {

    private ImageView closeIcon;
    private EditText productNameInput, productDescriptionInput, productPriceInput, productDiscountInput;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private Button addPicturesButton, saveButton;
    private TextView titleLabel;
    private LinearLayout eventTypesContainer;
    private RecyclerView photosRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;
    private List<String> currentEventTypes;
    private final Map<String, EventTypeDTO> eventTypeMap = new HashMap<>();

    private String name, description;
    private double price, discount;
    private boolean visible, available;
    private int offerId;

    private OnOfferUpdatedListener listener;

    public interface OnOfferUpdatedListener {
        void onOfferUpdated();
    }

    public void setOnOfferUpdatedListener(OnOfferUpdatedListener listener) {
        this.listener = listener;
    }

    public static EditProductDialog newInstance(Offer o) {
        EditProductDialog dialog = new EditProductDialog();
        Bundle args = new Bundle();
        args.putString("name", o.getName());
        args.putString("description", o.getDescription());
        args.putDouble("price", o.getPrice());
        args.putDouble("discount", o.getSale() != null ? o.getSale() : 0.0);
        args.putBoolean("visible", o.getIsVisible() != null && o.getIsVisible());
        args.putBoolean("available", o.getIsAvailable() != null && o.getIsAvailable());
        args.putInt("offerId", o.getId());
        ArrayList<String> et = new ArrayList<>();
        for (EventType e : o.getEventTypes()) et.add(e.getName());
        args.putStringArrayList("eventTypes", et);
        args.putStringArrayList("photos", new ArrayList<>(o.getPhotos()));
        dialog.setArguments(args);
        return dialog;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            name = getArguments().getString("name");
            description = getArguments().getString("description");
            price = getArguments().getDouble("price");
            discount = getArguments().getDouble("discount");
            visible = getArguments().getBoolean("visible");
            available = getArguments().getBoolean("available");
            offerId = getArguments().getInt("offerId");
            currentEventTypes = getArguments().getStringArrayList("eventTypes");
            photoUrls = getArguments().getStringArrayList("photos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_edit_product, container, false);

        closeIcon = view.findViewById(R.id.close_icon);
        titleLabel = view.findViewById(R.id.dialog_title);
        productNameInput = view.findViewById(R.id.product_name_input);
        productDescriptionInput = view.findViewById(R.id.product_description_input);
        productPriceInput = view.findViewById(R.id.product_price_input);
        productDiscountInput = view.findViewById(R.id.product_discount_input);
        visibilityCheckbox = view.findViewById(R.id.visibility_checkbox);
        availabilityCheckbox = view.findViewById(R.id.availability_checkbox);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);
        saveButton = view.findViewById(R.id.submit_button);
        eventTypesContainer = view.findViewById(R.id.event_types_container);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);

        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        setupListeners();
        populateFields();
        loadEventTypes();
        return view;
    }

    private void populateFields() {
        titleLabel.setText("Edit Product");
        productNameInput.setText(name);
        productDescriptionInput.setText(description);
        productPriceInput.setText(String.valueOf(price));
        productDiscountInput.setText(String.valueOf(discount));
        visibilityCheckbox.setChecked(visible);
        availabilityCheckbox.setChecked(available);
    }

    private void setupListeners() {
        closeIcon.setOnClickListener(v -> dismiss());
        addPicturesButton.setOnClickListener(v -> addPhotoUrl());
        saveButton.setOnClickListener(v -> handleSave());
    }

    private void addPhotoUrl() {
        EditText input = new EditText(getContext());
        input.setHint("Enter Photo URL");
        new AlertDialog.Builder(getContext())
                .setTitle("Add Photo URL")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        photoUrls.add(url);
                        photoAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadEventTypes() {
        eventTypesContainer.removeAllViews();
        ClientUtils.eventTypeService.getAll().enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (EventTypeDTO eventType : response.body()) {
                        eventTypeMap.put(eventType.getName(), eventType);
                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(eventType.getName());
                        checkBox.setTag(eventType.getName());
                        if (currentEventTypes.contains(eventType.getName())) checkBox.setChecked(true);
                        eventTypesContainer.addView(checkBox);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load event types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSave() {
        try {
            String newName = productNameInput.getText().toString().trim();
            String newDescription = productDescriptionInput.getText().toString().trim();
            double newPrice = Double.parseDouble(productPriceInput.getText().toString().trim());
            double newDiscount = Double.parseDouble(productDiscountInput.getText().toString().trim());
            boolean newVisible = visibilityCheckbox.isChecked();
            boolean newAvailable = availabilityCheckbox.isChecked();

            List<EventTypeDTO> selected = new ArrayList<>();
            for (int i = 0; i < eventTypesContainer.getChildCount(); i++) {
                View v = eventTypesContainer.getChildAt(i);
                if (v instanceof CheckBox && ((CheckBox) v).isChecked()) {
                    String tag = (String) v.getTag();
                    selected.add(eventTypeMap.get(tag));
                }
            }

            if (newName.isEmpty() || newDescription.isEmpty() || photoUrls.isEmpty() || selected.isEmpty()) {
                showSnackbar("Please complete all fields and select at least one event type.");
                return;
            }

            NewOfferDTO dto = new NewOfferDTO();
            dto.setName(newName);
            dto.setDescription(newDescription);
            dto.setPrice(newPrice);
            dto.setSale(newDiscount);
            dto.setIsVisible(newVisible);
            dto.setIsAvailable(newAvailable);
            dto.setPhotos(photoUrls);
            dto.setEventTypes(selected);

            saveButton.setEnabled(false);
            ClientUtils.offerService.editProviderProduct(offerId, dto).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        if (listener != null) listener.onOfferUpdated();
                        showSnackbar("Product updated successfully.");
                        dismiss();
                    } else {
                        showSnackbar("Failed to update product.");
                        saveButton.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<Offer> call, Throwable t) {
                    Log.e("EditProduct", "Error: " + t.getMessage());
                    showSnackbar("Server error.");
                    saveButton.setEnabled(true);
                }
            });

        } catch (Exception e) {
            Log.e("EditProductDialog", "Error saving", e);
            showSnackbar("Invalid input.");
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
    }
}
