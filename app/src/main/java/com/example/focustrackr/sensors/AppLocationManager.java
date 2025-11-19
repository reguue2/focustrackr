package com.example.focustrackr.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class AppLocationManager {

    public interface LocationCallback {
        void onLocationResult(double lat, double lon);
    }

    private static final int REQUEST_LOCATION_CODE = 1001;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    public AppLocationManager(Activity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void requestLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE
            );
            callback.onLocationResult(0, 0);
            return;
        }
        getLastLocation(callback);
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation(final LocationCallback callback) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        callback.onLocationResult(location.getLatitude(), location.getLongitude());
                    } else {
                        callback.onLocationResult(0, 0);
                    }
                })
                .addOnFailureListener(e -> callback.onLocationResult(0, 0));
    }
}
