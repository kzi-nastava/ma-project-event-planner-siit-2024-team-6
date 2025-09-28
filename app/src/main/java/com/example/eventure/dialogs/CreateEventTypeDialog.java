package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.Category;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventTypeDialog extends DialogFragment {

    private EditText nameInput, descriptionInput;
    private Button submitButton;
    private ImageView closeIcon;
    private OnEventTypeCreatedListener listener;
    private Spinner categorySpinner;
    private List<Category> allCategories = new ArrayList<>();
    private List<Category> selectedCategories = new ArrayList<>();

    private ArrayAdapter<String> spinnerAdapter;

    private Button addCategoryButton;
    private LinearLayout selectedCategoryLayout;
    public interface OnEventTypeCreatedListener {
        void onEventTypeCreated();
    }

    public void setOnEventTypeCreatedListener(OnEventTypeCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event_type, container, false);

        nameInput = view.findViewById(R.id.event_type_name_input);
        descriptionInput = view.findViewById(R.id.event_type_description_input);
        submitButton = view.findViewById(R.id.submit_event_type_button);
        closeIcon = view.findViewById(R.id.close_icon);

        closeIcon.setOnClickListener(v -> dismiss());


        categorySpinner = view.findViewById(R.id.category_spinner);
        addCategoryButton = view.findViewById(R.id.add_category_button);
        selectedCategoryLayout = view.findViewById(R.id.selected_category_container);

        loadCategories();

        addCategoryButton.setOnClickListener(v -> {
            int pos = categorySpinner.getSelectedItemPosition();
            if (pos >= 0 && pos < allCategories.size()) {
                Category selected = allCategories.get(pos);

                if (!selectedCategories.contains(selected)) {
                    selectedCategories.add(selected);

                    TextView categoryView = new TextView(getContext());
                    categoryView.setText("• " + selected.getName());
                    categoryView.setTextSize(16);
                    categoryView.setPadding(8, 8, 8, 8);
                    categoryView.setBackgroundColor(getResources().getColor(R.color.purple));
                    categoryView.setOnClickListener(removeView -> {
                        selectedCategories.remove(selected);
                        selectedCategoryLayout.removeView(categoryView);
                        showSnackbar("Removed: " + selected.getName());
                    });

                    selectedCategoryLayout.addView(categoryView);
                } else {
                    showSnackbar("Category already added");
                }
            }
        });


        submitButton.setOnClickListener(v -> handleSubmit());
        return view;
    }

    private void handleSubmit() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            showSnackbar("Please fill in all fields");
            return;
        }

        EventTypeDTO newEventType = new EventTypeDTO();
        newEventType.setName(name);
        newEventType.setDescription(description);
        newEventType.setDeleted(false);
        newEventType.setCategories(selectedCategories);

        submitButton.setEnabled(false);

        ClientUtils.adminService.createEventType(newEventType).enqueue(new Callback<EventTypeDTO>() {
            @Override
            public void onResponse(Call<EventTypeDTO> call, Response<EventTypeDTO> response) {
                if (response.isSuccessful()) {
                    showSnackbar("Event type created");
                    if (listener != null) listener.onEventTypeCreated();
                    dismiss();
                } else {
                    showSnackbar("Creation failed");
                    Log.e("CreateEventType", "Response code: " + response.code());
                    submitButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<EventTypeDTO> call, Throwable t) {
                showSnackbar("Error: " + t.getMessage());
                Log.e("CreateEventType", "Failure: ", t);
                submitButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
    private void loadCategories() {
        ClientUtils.adminService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    allCategories = new ArrayList<>();
                    List<String> categoryNames = new ArrayList<>();

                    for (Category c : response.body()) {
                        if (c != null && c.getName() != null) {
                            allCategories.add(c);
                            categoryNames.add(c.getName());
                        }
                    }

                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(spinnerAdapter);
                } else {
                    Log.e("CreateEventTypeDialog", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("CreateEventTypeDialog", "Failed to load categories", t);
            }
        });
    }

}
