package ru.dfax214.geolocation;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.Permission;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    Button goToSettings, startTracking, stopTracking, updateButton, changeDataButton;
    TextView periodTextView;
    boolean isServiceStopped = true;
    public final static int FINE_LOCATION_ACCESS = 1;
    public final static int BACKGROUND_LOCATION_ACCESS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText latitude1Edit = findViewById(R.id.latitude);
        EditText longitude1Edit = findViewById(R.id.longitude);

        SharedPreferences regionsPrefs = getSharedPreferences("regions", MODE_PRIVATE);
        try {
            JSONObject region = new JSONObject(regionsPrefs.getString("Район 1", ""));
            latitude1Edit.setText(String.valueOf(region.getDouble("latitude3")));
            longitude1Edit.setText(String.valueOf(region.getDouble("longitude3")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent accessIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            accessIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(accessIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_ACCESS);
            Log.d("check", String.valueOf(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    BACKGROUND_LOCATION_ACCESS);
            Log.d("check", String.valueOf(checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)));
        }
        if (isServiceStopped) {
            intent = new Intent(MainActivity.this, TrackerService.class);
            startService(intent);
            isServiceStopped = false;
        }

        goToSettings = findViewById(R.id.settings_button);
        goToSettings.setOnClickListener(view -> {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        startTracking = findViewById(R.id.start_tracking);
        startTracking.setOnClickListener(view -> {
            isServiceStopped = false;
            intent = new Intent(MainActivity.this, TrackerService.class);
            startService(intent);
        });
        stopTracking = findViewById(R.id.stop_tracking);
        stopTracking.setOnClickListener(view -> {
            isServiceStopped = true;
            stopService(new Intent(MainActivity.this, TrackerService.class));
        });
        updateButton = findViewById(R.id.update);
        updateButton.setOnClickListener(view -> {
            SharedPreferences regions = getSharedPreferences("regions", MODE_PRIVATE);
            String regionAsString = regions.getString("Район 1", "");
            Log.d("regionAsString", regionAsString);
            try {
                JSONObject regionAsJson = new JSONObject(regionAsString);
                periodTextView = findViewById(R.id.period_in_region);
                periodTextView.setText(String.valueOf(Math.toIntExact(regionAsJson.getLong("period") / 60000)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        changeDataButton = findViewById(R.id.change_data);
        changeDataButton.setOnClickListener(view -> {
            SharedPreferences regions = getSharedPreferences("regions", MODE_PRIVATE);
            try {
                Region regionAsJson = new Region();
                regionAsJson.put("latitude1", Double.parseDouble(latitude1Edit.getText().toString())-0.0005);
                regionAsJson.put("latitude2", Double.parseDouble(latitude1Edit.getText().toString())+0.0005);
                regionAsJson.put("longitude1", Double.parseDouble(longitude1Edit.getText().toString())-0.0005);
                regionAsJson.put("longitude2", Double.parseDouble(longitude1Edit.getText().toString())+0.0005);
                regionAsJson.put("latitude3", Double.parseDouble(latitude1Edit.getText().toString()));
                regionAsJson.put("longitude3", Double.parseDouble(longitude1Edit.getText().toString()));


                regionAsJson.put("period", 0);
                regions.edit().putString("Район 1", regionAsJson.toString()).apply();
                Log.d("regionAsJson", regionAsJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (!isServiceStopped) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartService");
            broadcastIntent.setClass(MainActivity.this, ServiceStarter.class);
            this.sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }
}