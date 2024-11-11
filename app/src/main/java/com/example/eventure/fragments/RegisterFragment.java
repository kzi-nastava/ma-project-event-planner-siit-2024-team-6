package com.example.eventure.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.eventure.R;


public class RegisterFragment extends Fragment {

    private Button btnSwitchToProvider;
    private LinearLayout providerFields;
    private boolean isProvider = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        providerFields = view.findViewById(R.id.providerFields);
        btnSwitchToProvider = view.findViewById(R.id.btnSwitchToProvider);

        btnSwitchToProvider.setOnClickListener(v -> {
            isProvider = !isProvider;
            providerFields.setVisibility(isProvider ? View.VISIBLE : View.GONE);
            btnSwitchToProvider.setText(isProvider ? "Register as Organizer" : "Register as Provider");
        });

        return view;
    }
}
