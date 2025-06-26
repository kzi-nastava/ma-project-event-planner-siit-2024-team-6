package com.example.eventure.dialogs;

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
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventTypeDialog extends DialogFragment {

    private ImageView closeIcon;
    private EditText nameInput, descriptionInput;
    private Spinner categorySpinner;
    private Button addCategoryButton;
    private LinearLayout selectedCategoryContainer;
    private Button submitButton;

    private int id;
    private String name, description;
    private boolean isDeleted;
    private List<Category> selectedCategories = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();

    public static EditEventTypeDialog newInstance(EventType eventType) {
        EditEventTypeDialog dialog = new EditEventTypeDialog();
        Bundle args = new Bundle();

        args.putInt("id", eventType.getId() != null ? eventType.getId() : -1);
        args.putString("name", eventType.getName());
        args.putString("description", eventType.getDescription());
        args.putBoolean("isDeleted", eventType.getIsDeleted() != null && eventType.getIsDeleted());

        ArrayList<String> catNames = new ArrayList<>();
        if (eventType.getCategories() != null) {
            for (Category category : eventType.getCategories()) {
                if (category.getName() != null)
                    catNames.add(category.getName());
            }
        }
        args.putStringArrayList("categories", catNames);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            name = getArguments().getString("name");
            description = getArguments().getString("description");
            isDeleted = getArguments().getBoolean("isDeleted", false);
            ArrayList<String> catNames = getArguments().getStringArrayList("categories");
            if (catNames != null) {
                for (String name : catNames) {
                    Category c = new Category();
                    c.setName(name);
                    selectedCategories.add(c);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_event_type, container, false);

        closeIcon = view.findViewById(R.id.close_icon);
        nameInput = view.findViewById(R.id.event_type_name_input);
        descriptionInput = view.findViewById(R.id.event_type_description_input);
        categorySpinner = view.findViewById(R.id.category_spinner); // ВАЖНО: добавь Spinner в xml
        addCategoryButton = view.findViewById(R.id.add_category_button);
        selectedCategoryContainer = view.findViewById(R.id.selected_category_container);
        submitButton = view.findViewById(R.id.submit_event_type_button);

        nameInput.setText(name);
        descriptionInput.setText(description);

        closeIcon.setOnClickListener(v -> dismiss());
        submitButton.setOnClickListener(v -> handleUpdate());

        // Загрузка всех категорий
        ClientUtils.adminService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item,
                            allCategories.stream().map(Category::getName).collect(Collectors.toList()));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("EditEventTypeDialog", "Failed to load categories: " + t.getMessage());
            }
        });

        addCategoryButton.setOnClickListener(v -> {
            int pos = categorySpinner.getSelectedItemPosition();
            if (pos >= 0 && pos < allCategories.size()) {
                Category selected = allCategories.get(pos);
                if (!selectedCategories.contains(selected)) {
                    selectedCategories.add(selected);
                    refreshCategoryList();
                } else {
                    showSnackbar("Category already added");
                }
            }
        });

        refreshCategoryList();
        return view;
    }

    private void refreshCategoryList() {
        selectedCategoryContainer.removeAllViews();
        for (Category category : selectedCategories) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_category_chip, selectedCategoryContainer, false);
            TextView text = item.findViewById(R.id.category_name);
            ImageView remove = item.findViewById(R.id.remove_category_icon);

            text.setText(category.getName());
            remove.setOnClickListener(v -> {
                selectedCategories.remove(category);
                refreshCategoryList();
            });

            selectedCategoryContainer.addView(item);
        }
    }

    private void handleUpdate() {
        String updatedName = nameInput.getText().toString().trim();
        String updatedDescription = descriptionInput.getText().toString().trim();

        if (updatedName.isEmpty() || updatedDescription.isEmpty()) {
            showSnackbar("Please fill in all fields");
            return;
        }

        EventTypeDTO dto = new EventTypeDTO();
        dto.setName(updatedName);
        dto.setDescription(updatedDescription);
        dto.setDeleted(isDeleted);
        dto.setCategories(selectedCategories);

        submitButton.setEnabled(false);

        ClientUtils.adminService.updateEventType(id, dto).enqueue(new Callback<EventTypeDTO>() {
            @Override
            public void onResponse(Call<EventTypeDTO> call, Response<EventTypeDTO> response) {
                if (response.isSuccessful()) {
                    if (listener != null) listener.onEventTypeUpdated();
                    showSnackbar("Event type updated successfully");
                    dismiss();
                } else {
                    showSnackbar("Failed to update");
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<EventTypeDTO> call, Throwable t) {
                Log.e("EditEventTypeDialog", "Error: " + t.getMessage());
                showSnackbar("Error: " + t.getMessage());
                submitButton.setEnabled(true);
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    public interface OnEventTypeUpdatedListener {
        void onEventTypeUpdated();
    }

    private OnEventTypeUpdatedListener listener;

    public void setOnEventTypeUpdatedListener(OnEventTypeUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
