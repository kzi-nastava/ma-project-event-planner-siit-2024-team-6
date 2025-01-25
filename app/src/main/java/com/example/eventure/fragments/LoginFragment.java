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

import com.example.eventure.BuildConfig;
import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.activities.ProfileActivity;
import com.example.eventure.clients.AuthService;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.EventService;
import com.example.eventure.clients.LoginService;
import com.example.eventure.dto.LoginDTO;
import com.example.eventure.dto.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnLogin;

    public LoginFragment() {
        // Required empty public constructor
    }

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

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(email);
            loginDTO.setPassword(password);

            ClientUtils.loginService.login(loginDTO).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                    if (response.code() == 200){
                        AuthService authService = new AuthService(getContext());
                        authService.login(response.body().getToken());
                        Log.d("AuthTag","Login successful, role  "+authService.getRole());
                        Intent intent = new Intent(requireContext(), HomeActivity.class);
                        startActivity(intent);

                    }else{
                        Log.d("AuthTag","Meesage recieved: "+response.code());
                        Toast.makeText(getContext(), "Please enter correct email and password", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    Log.d("AuthTag", t.getMessage() != null?t.getMessage():"error");

                }
            });



        });

        return view;
    }

}