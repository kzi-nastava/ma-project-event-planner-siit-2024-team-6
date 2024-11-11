package com.example.eventure.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventure.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private LinearLayout providerFields;
    private Button btnSwitchToProvider, btnRegister;
    private boolean isProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Инициализация общих и дополнительных полей
        providerFields = findViewById(R.id.providerFields);
        btnSwitchToProvider = findViewById(R.id.btnSwitchToProvider);
        btnRegister = findViewById(R.id.btnRegister);

        // Обработка кнопки переключения на Провайдера
        btnSwitchToProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProvider = !isProvider;
                providerFields.setVisibility(isProvider ? View.VISIBLE : View.GONE);
                btnSwitchToProvider.setText(isProvider ? "Register like Organizer" : "Register like Provider");
            }
        });

        // Обработка регистрации
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProvider) {
                    // Обработка регистрации провайдера
                    registerProvider();
                } else {
                    // Обработка регистрации организатора
                    registerOrganizer();
                }
            }
        });
    }

    private void registerOrganizer() {
        String name = ((EditText) findViewById(R.id.etName)).getText().toString().trim();
        String surname = ((EditText) findViewById(R.id.etSurname)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Registration", "Organizer registered: " + name + " " + surname + ", " + email);
        // Further logic for saving organizer data
    }

    private void registerProvider() {
        String name = ((EditText) findViewById(R.id.etName)).getText().toString().trim();
        String surname = ((EditText) findViewById(R.id.etSurname)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
        String companyName = ((EditText) findViewById(R.id.etCompanyName)).getText().toString().trim();
        String companyEmail = ((EditText) findViewById(R.id.etCompanyEmail)).getText().toString().trim();
        String companyAddress = ((EditText) findViewById(R.id.etCompanyAddress)).getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() ||
                companyName.isEmpty() || companyEmail.isEmpty() || companyAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields for Provider", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Registration", "Provider registered: " + companyName + ", " + companyEmail + ", " + companyAddress);
        // Further logic for saving provider data
    }


}
