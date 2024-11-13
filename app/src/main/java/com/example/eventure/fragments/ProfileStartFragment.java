package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventure.R;


public class ProfileStartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_start, container, false);

        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);

        // When the Login button is clicked, navigate to LoginFragment using NavController
        btnLogin.setOnClickListener(v -> {
            // Find the NavController from the current view
            NavController navController = Navigation.findNavController(v);
            // Navigate to the LoginFragment using the defined action in the navigation graph
            navController.navigate(R.id.action_profileStartFragment_to_loginFragment);
        });

        // When the Register button is clicked, navigate to RegisterFragment
        btnRegister.setOnClickListener(v -> {
            // Find the NavController from the current view
            NavController navController = Navigation.findNavController(v);
            // Navigate to the RegisterFragment using the defined action in the navigation graph
            navController.navigate(R.id.action_profileStartFragment_to_registerFragment);
        });

        return view;
    }
}
