package com.example.eventure.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;


public class RegisterFragment extends Fragment {

    private Button btnSwitchToProvider;
    private LinearLayout providerFields;
    private boolean isProvider = false;
    private Button btnRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        providerFields = view.findViewById(R.id.providerFields);
        btnSwitchToProvider = view.findViewById(R.id.btnSwitchToProvider);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnSwitchToProvider.setOnClickListener(v -> {
            isProvider = !isProvider;
            providerFields.setVisibility(isProvider ? View.VISIBLE : View.GONE);
            btnSwitchToProvider.setText(isProvider ? "Register as Organizer" : "Register as Provider");
        });

        btnRegister.setOnClickListener(v -> {
//            String email = etEmail.getText().toString();
//            String password = etPassword.getText().toString();
//            if (email.isEmpty() || password.isEmpty()) {
//                Log.d("Login", "Email or Password is empty");
//                // Показать сообщение об ошибке пользователю
//            } else {
//                Log.d("Login", "User logged in with email: " + email);
//                // Логика входа (например, аутентификация)
//            }


            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
