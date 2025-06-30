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
import com.example.eventure.dto.NewCategoryDTO;
import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.model.Offer;
import com.example.eventure.model.Status;
import com.google.android.material.snackbar.Snackbar;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProductDialog extends DialogFragment {

    private ImageView closeIcon;
    private EditText productNameInput, productDescriptionInput, productPriceInput, productDiscountInput;
    private Spinner productCategorySpinner;
    private CheckBox visibilityCheckbox, availabilityCheckbox;
    private Button addPicturesButton, submitButton, proposeCategoryButton;
    private RecyclerView photosRecyclerView;
    private LinearLayout eventTypesContainer;
    private PhotoAdapter photoAdapter;
    private List<String> photoUrls;
    private final Map<String, EventTypeDTO> eventTypeMap = new HashMap<>();

    private OnOfferCreatedListener listener;
    private List<String> categoryList = new ArrayList<>();
    private NewCategoryDTO proposedCategory = null;

    public interface OnOfferCreatedListener {
        void onOfferCreated();
    }

    public void setOnOfferCreatedListener(OnOfferCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_edit_product, container, false);

        initializeViews(view);
        loadCategories();

        photoUrls = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoUrls);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);

        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        closeIcon = view.findViewById(R.id.close_icon);
        proposeCategoryButton = view.findViewById(R.id.propose_category_button);
        productNameInput = view.findViewById(R.id.product_name_input);
        productDescriptionInput = view.findViewById(R.id.product_description_input);
        productPriceInput = view.findViewById(R.id.product_price_input);
        productDiscountInput = view.findViewById(R.id.product_discount_input);
        productCategorySpinner = view.findViewById(R.id.product_category_spinner);
        visibilityCheckbox = view.findViewById(R.id.visibility_checkbox);
        availabilityCheckbox = view.findViewById(R.id.availability_checkbox);
        addPicturesButton = view.findViewById(R.id.add_pictures_button);
        submitButton = view.findViewById(R.id.submit_button);
        photosRecyclerView = view.findViewById(R.id.photos_recycler_view);
        eventTypesContainer = view.findViewById(R.id.event_types_container);
    }

    private void setupListeners() {
        closeIcon.setOnClickListener(v -> dismiss());

        proposeCategoryButton.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_propose_category, null);
            EditText nameInput = dialogView.findViewById(R.id.category_name_input);
            EditText descInput = dialogView.findViewById(R.id.category_description_input);

            new AlertDialog.Builder(getContext())
                    .setTitle("Propose New Category")
                    .setView(dialogView)
                    .setPositiveButton("Submit", (dialog, which) -> {
                        String name = nameInput.getText().toString().trim();
                        String desc = descInput.getText().toString().trim();
                        if (!name.isEmpty()) {
                            proposedCategory = new NewCategoryDTO(name, desc);
                            proposeCategoryButton.setEnabled(false);
                            safeShowSnackbar("Proposed: " + name);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        addPicturesButton.setOnClickListener(v -> {
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
        });

        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = categoryList.get(position);
                loadEventTypesByCategoryName(selectedCategoryName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void loadCategories() {
        ClientUtils.categoryService.getAllCategoryNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    productCategorySpinner.setAdapter(adapter);

                    if (!categoryList.isEmpty()) {
                        productCategorySpinner.setSelection(0); // вызовет onItemSelected
                        loadEventTypesByCategoryName(categoryList.get(0)); // безопасный вызов
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                safeShowSnackbar("Failed to load categories.");
            }
        });
    }


    private void loadEventTypesByCategoryName(String categoryName) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                Log.w("CreateDialog", "Empty category name passed to loadEventTypesByCategoryName");
                return;
            }
        eventTypesContainer.removeAllViews();
        eventTypeMap.clear();


        ClientUtils.eventTypeService.getByCategoryName(categoryName).enqueue(new Callback<List<EventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EventTypeDTO>> call, Response<List<EventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventTypeDTO> types = response.body();
                    if (types.isEmpty()) {
                        TextView noTypes = new TextView(getContext());
                        noTypes.setText("No event types for this category.");
                        eventTypesContainer.addView(noTypes);
                        return;
                    }
                    for (EventTypeDTO eventType : types) {
                        String name = eventType.getName();
                        if (name == null || name.trim().isEmpty()) continue;

                        eventTypeMap.put(name, eventType);
                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(name);
                        checkBox.setTag(name);
                        eventTypesContainer.addView(checkBox);
                    }
                } else {
                    safeShowSnackbar("Failed to load event types.");
                }
            }

            @Override
            public void onFailure(Call<List<EventTypeDTO>> call, Throwable t) {
                Log.e("CreateDialog", "Event type loading failed", t);
                safeShowSnackbar("Failed to load event types.");
            }
        });
    } catch (Exception e) {
        Log.e("CreateDialog", "loadEventTypesByCategoryName threw exception", e);
    }
    }

    private void handleSubmit() {
        try {
            String name = productNameInput.getText().toString().trim();
            String desc = productDescriptionInput.getText().toString().trim();
            double price = Double.parseDouble(productPriceInput.getText().toString().trim());
            double discount = Double.parseDouble(productDiscountInput.getText().toString().trim());
            boolean visible = visibilityCheckbox.isChecked();
            boolean available = availabilityCheckbox.isChecked();

            String category = null;
            if (proposedCategory == null) {
                String selected = productCategorySpinner.getSelectedItem().toString();
                if (selected.isEmpty()) {
                    safeShowSnackbar("Please select or propose a category.");
                    return;
                }
                category = selected;
            }

            List<EventTypeDTO> selectedTypes = new ArrayList<>();
            for (int i = 0; i < eventTypesContainer.getChildCount(); i++) {
                View view = eventTypesContainer.getChildAt(i);
                if (view instanceof CheckBox && ((CheckBox) view).isChecked()) {
                    String eventTypeName = (String) view.getTag();
                    EventTypeDTO dto = eventTypeMap.get(eventTypeName);
                    if (dto != null) {
                        selectedTypes.add(dto);
                    } else {
                        Log.w("CreateDialog", "No DTO for selected event type: " + eventTypeName);
                    }
                }
            }

            if (name.isEmpty() || desc.isEmpty() || photoUrls.isEmpty() || selectedTypes.isEmpty()) {
                safeShowSnackbar("Please fill all required fields and select event types.");
                return;
            }

            NewOfferDTO newOffer = new NewOfferDTO();
            newOffer.setName(name);
            newOffer.setDescription(desc);
            newOffer.setPrice(price);
            newOffer.setSale(discount);
            newOffer.setPhotos(photoUrls);
            newOffer.setIsVisible(visible);
            newOffer.setIsAvailable(available);
            newOffer.setIsDeleted(false);
            newOffer.setCategory(category);
            newOffer.setCategorySuggestion(proposedCategory);
            newOffer.setEventTypes(selectedTypes);
            newOffer.setStatus(proposedCategory != null ? Status.PENDING : Status.ACCEPTED);

            ClientUtils.offerService.createProviderProduct(newOffer).enqueue(new Callback<Offer>() {
                @Override
                public void onResponse(Call<Offer> call, Response<Offer> response) {
                    if (response.isSuccessful()) {
                        if (listener != null) listener.onOfferCreated();
                        safeShowSnackbar("Product created successfully.");
                        dismiss();
                    } else {
                        safeShowSnackbar("Creation failed.");
                    }
                }

                @Override
                public void onFailure(Call<Offer> call, Throwable t) {
                    Log.e("CreateProduct", "Error: " + t.getMessage());
                    safeShowSnackbar("Server error.");
                }
            });

        } catch (Exception e) {
            Log.e("CreateProductDialog", "Submit error", e);
            safeShowSnackbar("Invalid input.");
        }
    }

    private void safeShowSnackbar(String msg) {
        if (getView() != null) {
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
        } else {
            Log.w("CreateProductDialog", "Snackbar skipped: " + msg);
        }
    }
}
