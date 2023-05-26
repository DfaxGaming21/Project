package ru.dfax214.geolocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Button applySettings;
    EditText periodEdit;
    SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        setContentView(R.layout.setings_activity);
        periodEdit = findViewById(R.id.settings_period_edit);
        applySettings = findViewById(R.id.apply_settings);
        applySettings.setOnClickListener(view -> {
            int period = Integer.parseInt(periodEdit.getText().toString());
            Log.d("period", String.valueOf(period));
            if (period > 0) {
                settings.edit().putInt("period", period).apply();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Период должен быть больше нуля", Toast.LENGTH_SHORT).show();
            }
        });
    }
}