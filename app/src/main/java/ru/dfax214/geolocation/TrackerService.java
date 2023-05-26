package ru.dfax214.geolocation;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerService extends Service {
    LocationManager locationManager;
    SharedPreferences settings, regions;
    int period;
    long periodAsMillis;
    protected Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        period = settings.getInt("period", 10);
        regions = getSharedPreferences("regions", MODE_PRIVATE);
        periodAsMillis = period * 60000L;
        timer.schedule(timerTask, 0, periodAsMillis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service is destroyed", "service is destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.d("getting coordinates", "getting coordinates");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                onDestroy();
                Log.d("permission", "I don't have permission");
                return;
            }
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener, Looper.getMainLooper());
            Location location = locationManager.getLastKnownLocation("gps");
            if (location != null) {
                Map<String, ?> allRegions = regions.getAll();
                JSONObject regionAsJson = null;
                for (Map.Entry<String, ?> entry : allRegions.entrySet()) {
                    try {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        regionAsJson = new JSONObject((String) entry.getValue());
                        double latitude1 = regionAsJson.getDouble("latitude1");
                        double latitude2 = regionAsJson.getDouble("latitude2");
                        double longitude1 = regionAsJson.getDouble("longitude1");
                        double longitude2 = regionAsJson.getDouble("longitude2");
                        if (latitude1 < latitude && latitude2 > latitude && longitude1 < longitude && longitude2 > longitude) {
                            regionAsJson.put("latitude", latitude);
                            regionAsJson.put("longitude", longitude);
                            regionAsJson.put("period", regionAsJson.getLong("period") + periodAsMillis);
                            regions.edit().putString("Район 1", regionAsJson.toString()).apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("position", location.getLatitude() + " " + location.getLongitude() + " " + location.getTime());
            } else {
                Log.d("there are no location", "there are no location");
            }
        }
    };


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }
    };
}