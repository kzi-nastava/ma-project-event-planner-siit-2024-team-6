package com.example.eventure.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.RegistrationRequestDTO;
import com.example.eventure.dto.UserDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private Button btnSwitchToProvider;
    private LinearLayout providerFields;
    private boolean isProvider = false;
    private Button btnRegister;

    private EditText etName, etSurname, etEmail, etPassword, etAddress, etPhone;
    private EditText etCompanyName, etCompanyEmail, etCompanyAddress, etDescription, etOpeningTime, etClosingTime;
    private String uploadedUserPhotoUrl = null;
    private String uploadedCompanyPhotoUrl = null;

//    private ActivityResultLauncher<Intent> photoPickerLauncher;
//    private boolean isPickingUserPhoto = true; // флаг для определения, какую кнопку нажали


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Common fields
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);

        // Provider fields
        etCompanyName = view.findViewById(R.id.etCompanyName);
        etCompanyEmail = view.findViewById(R.id.etCompanyEmail);
        etCompanyAddress = view.findViewById(R.id.etCompanyAddress);
        etDescription = view.findViewById(R.id.etDescription);
        etOpeningTime = view.findViewById(R.id.etOpeningTime);
        etClosingTime = view.findViewById(R.id.etClosingTime);

        providerFields = view.findViewById(R.id.providerFields);
        btnSwitchToProvider = view.findViewById(R.id.btnSwitchToProvider);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnSwitchToProvider.setOnClickListener(v -> {
            isProvider = !isProvider;
            providerFields.setVisibility(isProvider ? View.VISIBLE : View.GONE);
            btnSwitchToProvider.setText(isProvider ? "Register as Organizer" : "Register as Provider");
        });
//        photoPickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri selectedImageUri = result.getData().getData();
//                        if (selectedImageUri != null) {
//                            String imageUrl = selectedImageUri.toString();
//                            if (isPickingUserPhoto) {
//                                uploadedUserPhotoUrl = imageUrl;
//                                Toast.makeText(requireContext(), "User photo selected", Toast.LENGTH_SHORT).show();
//                            } else {
//                                uploadedCompanyPhotoUrl = imageUrl;
//                                Toast.makeText(requireContext(), "Company photo selected", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }
//        );
        Button btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        Button btnUploadEventPhoto = view.findViewById(R.id.btnUploadEventPhoto);
        btnUploadPhoto.setOnClickListener(v -> showUrlDialog("Enter User Photo URL", url -> {
            uploadedUserPhotoUrl = url;
            Toast.makeText(requireContext(), "User photo set", Toast.LENGTH_SHORT).show();
        }));

        btnUploadEventPhoto.setOnClickListener(v -> showUrlDialog("Enter Company Photo URL", url -> {
            uploadedCompanyPhotoUrl = url;
            Toast.makeText(requireContext(), "Company photo set", Toast.LENGTH_SHORT).show();
        }));

//        btnUploadPhoto.setOnClickListener(v -> {
//            isPickingUserPhoto = true;
//            openImagePicker();
//        });
//
//        btnUploadEventPhoto.setOnClickListener(v -> {
//            isPickingUserPhoto = false;
//            openImagePicker();
//        });

        btnRegister.setOnClickListener(v -> submitRegistration());

        return view;
    }
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        photoPickerLauncher.launch(intent);
//    }
private void showUrlDialog(String title, UrlCallback callback) {
    EditText input = new EditText(requireContext());
    input.setHint("https://example.com/image.jpg");

    new android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("OK", (dialog, which) -> {
                String url = input.getText().toString().trim();
                if (!url.isEmpty()) {
                    callback.onUrlEntered(url);
                } else {
                    Toast.makeText(requireContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
}

    private void submitRegistration() {
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setName(etName.getText().toString().trim());
        dto.setLastname(etSurname.getText().toString().trim());
        dto.setEmail(etEmail.getText().toString().trim());
        dto.setPassword(etPassword.getText().toString().trim());
        dto.setAddress(etAddress.getText().toString().trim());
        dto.setPhoneNumber(etPhone.getText().toString().trim());
        dto.setRole(isProvider ? "PROVIDER" : "ORGANIZER");
        dto.setPhotoUrl( uploadedUserPhotoUrl != null ? uploadedUserPhotoUrl : "");

        if (isProvider) {
            dto.setCompanyName(etCompanyName.getText().toString().trim());
            dto.setCompanyEmail(etCompanyEmail.getText().toString().trim());
            dto.setCompanyAddress(etCompanyAddress.getText().toString().trim());
            dto.setDescription(etDescription.getText().toString().trim());
            dto.setOpeningTime(etOpeningTime.getText().toString().trim());
            dto.setClosingTime(etClosingTime.getText().toString().trim());
            dto.setCompanyPhoto(uploadedCompanyPhotoUrl != null ? uploadedCompanyPhotoUrl : "");

//            dto.setCompanyPhoto(new ArrayList<>()); // can be set later if needed
        }

        ClientUtils.userService.registerUser(dto).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserDTO> call, @NonNull Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                    Toast.makeText(requireContext(), "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.loginFragment); // Убедись, что ID совпадает с тем, что у тебя в nav_graph.xml

//                    startActivity(new Intent(requireContext(), HomeActivity.class));
                } else {
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    Log.e("Register", "Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDTO> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                Log.e("Register", "Network failure: ", t);
            }
        });
    }
}
interface UrlCallback {
    void onUrlEntered(String url);
}
