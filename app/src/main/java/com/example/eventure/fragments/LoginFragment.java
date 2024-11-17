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
import android.widget.EditText;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.activities.ProfileActivity;


public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Log.d("Login", "Email or Password is empty");
                // Показать сообщение об ошибке пользователю
            } else {
                Log.d("Login", "User logged in with email: " + email);
                // Логика входа (например, аутентификация)
            }


            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
        });

        return view;
    }
}