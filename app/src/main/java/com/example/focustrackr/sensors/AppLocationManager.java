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
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * Solicita ubicación. Si no hay permisos, los pide primero.
     */
    public void requestLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Pedimos permiso → NO asignamos ubicación aún
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE
            );
            return;
        }
        // Ya hay permisos, obtenemos ubicación directamente
        getLastLocation(callback);
    }

    /**
     * Se ejecuta cuando el usuario responde al permiso.
     */
    public void onRequestPermissionsResult(int requestCode, int[] grantResults, LocationCallback callback) {
        if (requestCode == REQUEST_LOCATION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Ahora que sí hay permiso → obtener ubicación
            getLastLocation(callback);
        } else {
            // Usuario rechaza → decidimos usar 0,0
            callback.onLocationResult(0, 0);
        }
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
