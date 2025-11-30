package com.example.focustrackr.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focustrackr.R;
import com.example.focustrackr.ui.auth.LoginActivity;
import com.example.focustrackr.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Actividad principal que gestiona la navegación mediante BottomNavigationView.
 */
public class MainActivity extends AppCompatActivity {

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recupera el email del usuario enviado desde LoginActivity.
        userEmail = getIntent().getStringExtra(Constants.EXTRA_USER_EMAIL);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        /**
         * Listener para cambiar fragmentos según la opción seleccionada en la barra inferior.
         */
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_sessions) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new SessionListFragment())
                        .commit();
                return true;

            } else if (id == R.id.nav_stats) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new StatsFragment())
                        .commit();
                return true;

            } else if (id == R.id.nav_goals) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new GoalsFragment())
                        .commit();
                return true;

            }else if (id == R.id.nav_location) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new LocationFragment())
                        .commit();
                return true;

            } else if (id == R.id.nav_logout) {
                confirmLogout();
                return true;
            }

            return false;
        });


        // Carga por defecto la lista de sesiones.
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_sessions);
        }
    }

    /**
     * Devuelve el email del usuario conectado.
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Muestra un diálogo de confirmación para cerrar sesión.
     * Si el usuario acepta, se limpia la sesión y se vuelve a LoginActivity.
     */
    private void confirmLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {

                    // Vibración breve como feedback de confirmación.
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
                    }

                    // Limpia datos de sesión.
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean(Constants.PREF_LOGGED_IN, false)
                            .remove(Constants.PREF_USER_EMAIL)
                            .apply();

                    // Redirige al login limpiando la pila de actividades.
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
