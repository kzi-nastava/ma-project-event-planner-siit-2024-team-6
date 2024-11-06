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
                btnSwitchToProvider.setText(isProvider ? "Зарегистрироваться как Организатор" : "Зарегистрироваться как Провайдер");
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
        String name = ((EditText) findViewById(R.id.etName)).getText().toString();
        String surname = ((EditText) findViewById(R.id.etSurname)).getText().toString();
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        // Соберите остальные данные организатора
        Log.d("Registration", "Организатор зарегистрирован: " + name + " " + surname + ", " + email);
    }

    private void registerProvider() {
        String companyName = ((EditText) findViewById(R.id.etCompanyName)).getText().toString();
        String companyEmail = ((EditText) findViewById(R.id.etCompanyEmail)).getText().toString();
        String companyAddress = ((EditText) findViewById(R.id.etCompanyAddress)).getText().toString();
        // Соберите остальные данные провайдера
        Log.d("Registration", "Провайдер зарегистрирован: " + companyName + ", " + companyEmail + ", " + companyAddress);
    }

}
