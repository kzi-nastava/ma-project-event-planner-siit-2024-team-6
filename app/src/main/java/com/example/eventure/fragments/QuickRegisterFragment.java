package com.example.eventure.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.QuickRegistrationDTO;
import com.example.eventure.dto.UserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuickRegisterFragment extends Fragment {

    private static final String ARG_EMAIL = "email";
    private static final String ARG_EVENT_ID = "eventId";


    private EditText qrName, qrSurname, qrEmail, qrPassword;
    private Button btnQuickRegister;

    private String email;
    private Integer eventId;


    public static QuickRegisterFragment newInstance(String email, int eventId) {
        QuickRegisterFragment fragment = new QuickRegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            eventId = getArguments().containsKey(ARG_EVENT_ID) ? getArguments().getInt(ARG_EVENT_ID) : null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_register, container, false);

        qrName = view.findViewById(R.id.qrName);
        qrSurname = view.findViewById(R.id.qrSurname);
        qrEmail = view.findViewById(R.id.qrEmail);
        qrPassword = view.findViewById(R.id.qrPassword);
        btnQuickRegister = view.findViewById(R.id.btnQuickRegister);

        if (email != null) {
            qrEmail.setText(email);
            qrEmail.setEnabled(false);
        }

        btnQuickRegister.setOnClickListener(v -> {
            registerUser();
        });

        return view;
    }

    private void registerUser() {
        String name = qrName.getText().toString().trim();
        String lastname = qrSurname.getText().toString().trim();
        String email = qrEmail.getText().toString().trim();
        String password = qrPassword.getText().toString();

        if (name.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        QuickRegistrationDTO dto = new QuickRegistrationDTO();
        dto.setName(name);
        dto.setLastname(lastname);
        dto.setEmail(email);
        if (eventId != null) {
            dto.setEventId(eventId);
        }
        dto.setPassword(password);

        ClientUtils.userService.quickRegister(dto).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserDTO> call, @NonNull Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    NavController navController = NavHostFragment.findNavController(QuickRegisterFragment.this);
                    Toast.makeText(requireContext(), "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    navController.navigate(R.id.loginFragment, bundle);
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
