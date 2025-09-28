package com.example.eventure.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.UserService;
import com.example.eventure.dto.PasswordChangeDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends DialogFragment {

    private EditText oldPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button changeButton;

    private UserService userService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_password, null);

        oldPasswordInput = view.findViewById(R.id.old_password_input);
        newPasswordInput = view.findViewById(R.id.new_password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        changeButton = view.findViewById(R.id.change_password_confirm_btn);

        userService = ClientUtils.retrofit.create(UserService.class);

        changeButton.setOnClickListener(v -> attemptPasswordChange());

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(view);
        dialog.setTitle("Change Password");
        return dialog;
    }

    private void attemptPasswordChange() {
        String oldPass = oldPasswordInput.getText().toString();
        String newPass = newPasswordInput.getText().toString();
        String confirmPass = confirmPasswordInput.getText().toString();

        if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setOldPassword(oldPass);
        dto.setNewPasswordFirst(newPass);
        dto.setNewPasswordSecond(confirmPass);

        changeButton.setEnabled(false);

        userService.changePassword(dto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                changeButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body(), Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Server error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                changeButton.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
