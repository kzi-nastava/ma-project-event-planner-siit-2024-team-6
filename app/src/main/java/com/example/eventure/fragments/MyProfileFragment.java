package com.example.eventure.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.UserService;
import com.example.eventure.dto.UserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProfileFragment extends Fragment {

    private EditText emailInput, roleInput, nameInput, lastnameInput, addressInput, phoneInput, photoUrlInput;
    private EditText companyEmailInput, companyAddressInput, descriptionInput, openingTimeInput, closingTimeInput, companyPhotosInput;

    private Button submitButton, changePasswordButton, logoutButton, deactivateButton;
    private ImageView profilePhoto;
    private TextView photoTitle, companyEmailLabel, companyAddressLabel, descriptionLabel, openingTimeLabel, closingTimeLabel;
    private LinearLayout photoContainer;
    private Button addPhotoButton;

    private UserService userService;

    public MyProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        logoutButton = view.findViewById(R.id.logout_button);

        emailInput = view.findViewById(R.id.email_input);
        roleInput = view.findViewById(R.id.role_input);
        nameInput = view.findViewById(R.id.name_input);
        lastnameInput = view.findViewById(R.id.lastname_input);
        addressInput = view.findViewById(R.id.address_input);
        phoneInput = view.findViewById(R.id.phone_input);
        photoUrlInput = view.findViewById(R.id.photo_url_input);
        profilePhoto = view.findViewById(R.id.profile_photo);
        companyEmailInput = view.findViewById(R.id.company_email_input);
        companyAddressInput = view.findViewById(R.id.company_address_input);
        descriptionInput = view.findViewById(R.id.description_input);
        openingTimeInput = view.findViewById(R.id.opening_time_input);
        closingTimeInput = view.findViewById(R.id.closing_time_input);
        companyPhotosInput = view.findViewById(R.id.company_photos_input); // Новый

        submitButton = view.findViewById(R.id.submit_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        logoutButton = view.findViewById(R.id.logout_button);
        deactivateButton = view.findViewById(R.id.deactivate_button);

        userService = ClientUtils.retrofit.create(UserService.class);
        photoContainer = view.findViewById(R.id.company_photos_container);
        addPhotoButton = view.findViewById(R.id.add_photo_button);
        photoTitle = view.findViewById(R.id.company_photos_title);

        photoTitle = view.findViewById(R.id.company_photos_title);
        photoContainer = view.findViewById(R.id.company_photos_container);
        addPhotoButton = view.findViewById(R.id.add_photo_button);
         companyEmailLabel = view.findViewById(R.id.company_email_label);
         companyAddressLabel = view.findViewById(R.id.company_address_label);
         descriptionLabel = view.findViewById(R.id.description_label);
         openingTimeLabel = view.findViewById(R.id.opening_time_label);
         closingTimeLabel = view.findViewById(R.id.closing_time_label);


        loadProfile();
        setupListeners();

        return view;
    }
    private void addPhotoInput(String url, LinearLayout container) {
        Context context = container.getContext();

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        row.setPadding(0, 16, 0, 16);

        // Image preview
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        row.addView(imageView);

        // Input
        EditText photoInput = new EditText(context);
        photoInput.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        photoInput.setHint("Photo URL");
        photoInput.setText(url);
        photoInput.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        row.addView(photoInput);

        // Delete button
        Button deleteButton = new Button(context);
        deleteButton.setText("Remove");
        deleteButton.setBackgroundColor(Color.parseColor("#B00020"));
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setOnClickListener(v -> container.removeView(row));
        row.addView(deleteButton);

        container.addView(row);

        // Load image from URL
        if (!url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_error)             // optional
                    .into(imageView);
        }

        // Live update image on URL change
        photoInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newUrl = photoInput.getText().toString().trim();
                if (!newUrl.isEmpty()) {
                    Glide.with(context)
                            .load(newUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .into(imageView);
                }
            }
        });
    }
    private void loadProfile() {
        userService.getProfile().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();

                    emailInput.setText(user.getEmail());
                    nameInput.setText(user.getName());
                    lastnameInput.setText(user.getLastname());
                    addressInput.setText(user.getAddress());
                    phoneInput.setText(user.getPhoneNumber());
                    photoUrlInput.setText(user.getPhotoUrl());
                    Glide.with(requireContext())
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .into(profilePhoto);
                    // Disable non-editable fields
                    emailInput.setEnabled(false);
                    roleInput.setEnabled(false);
                    companyEmailInput.setEnabled(false);

                    roleInput.setText(user.getUserType());

                    // Provider-specific fields
                    if ("Provider".equalsIgnoreCase(user.getUserType())) {
                        companyEmailInput.setVisibility(View.VISIBLE);
                        companyEmailInput.setText(user.getCompanyEmail());

                        companyAddressInput.setVisibility(View.VISIBLE);
                        descriptionInput.setVisibility(View.VISIBLE);
                        openingTimeInput.setVisibility(View.VISIBLE);
                        closingTimeInput.setVisibility(View.VISIBLE);
                        companyPhotosInput.setVisibility(View.GONE); // скрываем старое поле (если ещё не удалено из layout)

                        companyAddressInput.setText(user.getCompanyAddress());
                        descriptionInput.setText(user.getDescription());
                        openingTimeInput.setText(user.getOpeningTime());
                        closingTimeInput.setText(user.getClosingTime());

                        // 👇 Вот сюда вставь
                        photoTitle.setVisibility(View.VISIBLE);
                        photoContainer.setVisibility(View.VISIBLE);
                        addPhotoButton.setVisibility(View.VISIBLE);
                        companyEmailLabel.setVisibility(View.VISIBLE);
                        companyAddressLabel.setVisibility(View.VISIBLE);
                        descriptionLabel.setVisibility(View.VISIBLE);
                        openingTimeLabel.setVisibility(View.VISIBLE);
                        closingTimeLabel.setVisibility(View.VISIBLE);

                        // Очистим контейнер и добавим поля
                        photoContainer.removeAllViews();
                        for (String photoUrl : user.getCompanyPhotos()) {
                            addPhotoInput(photoUrl, photoContainer);
                        }

                        addPhotoButton.setOnClickListener(v -> addPhotoInput("", photoContainer));
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            submitButton.setEnabled(false);

            Map<String, Object> updated = new HashMap<>();
            updated.put("name", nameInput.getText().toString());
            updated.put("lastname", lastnameInput.getText().toString());
            updated.put("address", addressInput.getText().toString());
            updated.put("phoneNumber", phoneInput.getText().toString());
            updated.put("photoUrl", photoUrlInput.getText().toString());

            if ("Provider".equalsIgnoreCase(roleInput.getText().toString())) {
                Log.d("MyProfile", "User is provider, showing provider fields.");

                updated.put("companyAddress", companyAddressInput.getText().toString());
                updated.put("description", descriptionInput.getText().toString());
                updated.put("openingTime", openingTimeInput.getText().toString());
                updated.put("closingTime", closingTimeInput.getText().toString());
                LinearLayout photoContainer = requireView().findViewById(R.id.company_photos_container);
                List<String> photoUrls = new ArrayList<>();
                for (int i = 0; i < photoContainer.getChildCount(); i++) {
                    View row = photoContainer.getChildAt(i);
                    if (row instanceof LinearLayout) {
                        EditText input = (EditText) ((LinearLayout) row).getChildAt(1);
                        String text = input.getText().toString().trim();
                        if (!text.isEmpty()) {
                            photoUrls.add(text);
                        }
                    }
                }
                updated.put("companyPhotos", photoUrls);
            }
            userService.updateProfile(updated).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    submitButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    submitButton.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        changePasswordButton.setOnClickListener(v -> {
            ChangePasswordFragment dialog = new ChangePasswordFragment();
            dialog.show(getParentFragmentManager(), "ChangePasswordDialog");
        });
        deactivateButton.setOnClickListener(v -> {
            deactivateButton.setEnabled(false); // чтобы нельзя было нажимать повторно

            userService.deleteAccount().enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()) {
                        // Успешно удалён — теперь logout
                        ClientUtils.getAuthService().logout();

                        requireActivity()
                                .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.loginFragment);
                    } else {
                        deactivateButton.setEnabled(true);
                        Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    deactivateButton.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        logoutButton.setOnClickListener(v -> {
            // Очистка SharedPreferences
            requireActivity()
                    .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Логаут на сервере (если надо)

            ClientUtils.getAuthService().logout();
            NavController navController = Navigation.findNavController(requireView());
            // Замена текущего фрагмента на LoginFragment
            navController.navigate(R.id.loginFragment);

        });


    }
}

