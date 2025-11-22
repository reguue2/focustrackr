package com.example.focustrackr.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Clase encargada de gestionar la obtención de la ubicación
 * utilizando el proveedor de localización de Google Play Services.
 */
public class AppLocationManager {

    /**
     * Callback para devolver la posición obtenida.
     */
    public interface LocationCallback {
        void onLocationResult(double lat, double lon);
    }

    private static final int REQUEST_LOCATION_CODE = 1001;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    /**
     * Recibe la Activity desde la cual se solicitan los permisos y ubicación.
     */
    public AppLocationManager(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * Comprueba permisos de ubicación y, si existen, obtiene la posición.
     * Si no, solicita los permisos al usuario.
     */
    public void requestLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE
            );
            return;
        }
        getLastLocation(callback);
    }

    /**
     * Se llama tras la respuesta de solicitud de permisos.
     * Si se aceptan, se obtiene la ubicación.
     */
    public void onRequestPermissionsResult(int requestCode, int[] grantResults, LocationCallback callback) {
        if (requestCode == REQUEST_LOCATION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation(callback);
        } else {
            // Si el usuario no da permiso, se devuelve una ubicación por defecto.
            callback.onLocationResult(0, 0);
        }
    }

    /**
     * Obtiene la última ubicación conocida del dispositivo.
     * (Se asume permiso ya concedido)
     */
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
