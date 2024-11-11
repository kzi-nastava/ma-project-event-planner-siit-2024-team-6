package com.example.eventure.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.eventure.R;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email, password);
            }
        });
    }

    private void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Log.d("Login", "Email or Password is empty");
            // Показать сообщение об ошибке пользователю
        } else {
            Log.d("Login", "User logged in with email: " + email);
            // Логика входа (например, аутентификация)
        }
    }
}
