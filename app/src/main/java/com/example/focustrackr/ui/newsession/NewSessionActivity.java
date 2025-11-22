package com.example.focustrackr.ui.newsession;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.databinding.ActivityNewSessionBinding;
import com.example.focustrackr.sensors.AppLocationManager;
import com.example.focustrackr.sensors.FocusSensorManager;

/**
 * Actividad para iniciar y finalizar una nueva sesión de concentración.
 * Utiliza sensores para medir enfoque y ubicación del usuario.
 */
public class NewSessionActivity extends AppCompatActivity {

    private ActivityNewSessionBinding binding;
    private NewSessionViewModel viewModel;
    private FocusSensorManager focusSensorManager;
    private AppLocationManager locationManager;
    private boolean sessionStarted = false;
    private long sessionStartTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel y sensores
        viewModel = new ViewModelProvider(this).get(NewSessionViewModel.class);
        focusSensorManager = new FocusSensorManager(this);
        focusSensorManager.setFocusListener(viewModel::updateFocus);
        locationManager = new AppLocationManager(this);

        setupObservers();
        setupBackPressHandler();

        // Listeners de botones
        binding.btnStartSession.setOnClickListener(v -> startSession());
        binding.btnEndSession.setOnClickListener(v -> endSession());
    }

    /**
     * Observadores de estado y nivel de enfoque.
     */
    private void setupObservers() {
        viewModel.sessionState.observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case IDLE:
                    binding.chronometer.stop();
                    break;
                case RUNNING:
                    binding.chronometer.setBase(sessionStartTime);
                    binding.chronometer.start();
                    break;
                case FINISHED:
                    finish();
                    break;
            }
        });

        viewModel.focusLevel.observe(this, focus -> {
            if (focus == null) return;
            binding.pbFocusLevel.setProgress((int) (float) focus);
            binding.tvFocusPercentage.setText("Foco: " + (int) (float) focus + "%");
        });
    }

    /**
     * Gestiona el botón atrás para evitar salir sin confirmar si hay sesión activa.
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!sessionStarted) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(NewSessionActivity.this)
                        .setTitle("Sesión en progreso")
                        .setMessage("Si sales ahora, la sesión se perderá. ¿Quieres salir igualmente?")
                        .setPositiveButton("Salir", (d, w) -> {
                            sessionStarted = false;
                            focusSensorManager.stop();
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        })
                        .setNegativeButton("Continuar", (d, w) -> d.dismiss())
                        .setCancelable(false)
                        .create();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }
                dialog.show();
            }
        });
    }

    /**
     * Inicia la sesión si el nombre es válido.
     */
    private void startSession() {
        String name = binding.etSessionName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.etSessionName.setError(getString(R.string.new_session_need_name));
            return;
        }

        if (sessionStarted) {
            Toast.makeText(this, "La sesión ya está en marcha", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionStarted = true;
        sessionStartTime = SystemClock.elapsedRealtime();
        viewModel.startSession(sessionStartTime);

        focusSensorManager.startSafe();
        locationManager.requestLocation(viewModel::updateLocation);

        Toast.makeText(this, getString(R.string.new_session_started), Toast.LENGTH_SHORT).show();
    }

    /**
     * Finaliza la sesión y guarda los datos si es válida.
     */
    private void endSession() {
        if (!sessionStarted) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionStarted = false;
        focusSensorManager.stop();

        String name = binding.etSessionName.getText().toString().trim();
        int targetMinutes = 0;
        try {
            String durationStr = binding.etSessionDuration.getText().toString();
            targetMinutes = durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr);
        } catch (NumberFormatException ignored) {}

        boolean success = viewModel.endSession(name, targetMinutes);

        if (!success) {
            Toast.makeText(this, getString(R.string.new_session_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.new_session_saved), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Callback de respuesta a solicitud de permisos (ubicación).
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        locationManager.onRequestPermissionsResult(
                requestCode,
                grantResults,
                viewModel::updateLocation
        );
    }
}
