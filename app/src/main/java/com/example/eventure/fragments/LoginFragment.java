package com.example.eventure.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import com.example.eventure.clients.NotificationSocketManager;
import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.activities.ProfileActivity;
import com.example.eventure.clients.AuthService;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.EventService;
import com.example.eventure.clients.LoginService;
import com.example.eventure.dto.LoginDTO;
import com.example.eventure.dto.LoginResponseDTO;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private String receivedEmail = null;
    private Integer receivedEventId = null;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        if (getArguments() != null) {
            receivedEmail = getArguments().getString("email", null);
            if (getArguments().containsKey("eventId")) {
                receivedEventId = getArguments().getInt("eventId");
            }
        }
        Log.d("AuthTag","LOGIN PARAMS");
        Log.d("AuthTag",String.valueOf(receivedEmail));
        Log.d("AuthTag",String.valueOf(receivedEventId));

        etEmail = view.findViewById(R.id.etEmail);
        if( receivedEmail != null){
            etEmail.setText(receivedEmail);
            etEmail.setEnabled(false);
        }

        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty() || password.isEmpty()) {
                Log.d("Login", "Email or Password is empty");
                // Показать сообщение об ошибке пользователю
            } else {
                Log.d("Login", "User logged in with email: " + email);
                // Логика входа (например, аутентификация)
            }

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(email);
            loginDTO.setPassword(password);

            ClientUtils.loginService.login(loginDTO).enqueue(new Callback<LoginResponseDTO>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthService authService = new AuthService(getContext());
                        authService.login(response.body().getToken());
                        authService.saveMuted(response.body().isMuted());
                        Log.d("AuthTag", "Login successful, role: " + authService.getRole()+" ,muted: "+authService.isMuted());

                        int userId = authService.getUserId();
                        NotificationSocketManager.getInstance().disconnect();
                        NotificationSocketManager.getInstance().connect(requireContext().getApplicationContext(), userId);

                        // if login accessed by invitation link, accept invitation when user logged in
                        if (receivedEventId != null) {
                            joinEventAndNavigateHome(receivedEventId);
                        } else {
                            navigateToHome();
                        }

//                        Intent intent = new Intent(requireContext(), HomeActivity.class);
//                      startActivity(intent);
                    } else if (response.code() == 403) {
                        try {
                            String errorString = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorString);
                            String message = jsonObject.optString("message", "You are suspended. Please try again later.");
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "You are suspended. Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d("AuthTag", "Message received: " + response.code());
                        Toast.makeText(getContext(), "Please enter correct email and password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    Log.d("AuthTag", t.getMessage() != null ? t.getMessage() : "error");
                    Toast.makeText(getContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        return view;
    }
    private void joinEventAndNavigateHome(int eventId) {
        ClientUtils.eventService.participate(eventId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Successfully joined event.", Toast.LENGTH_SHORT).show();
                    Log.d("Login", "Successfully joined event with ID: " + eventId);
                } else {
                    Toast.makeText(getContext(), "You have already joined this event.", Toast.LENGTH_SHORT).show();
                    Log.e("Login", "Failed to join event, code: " + response.code());
                }
                navigateToHome();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Login", "Error joining event: " + t.getMessage());
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(requireContext(), HomeActivity.class);
        startActivity(intent);
    }


}