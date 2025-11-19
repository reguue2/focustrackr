package com.example.focustrackr.ui.newsession;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.databinding.ActivityNewSessionBinding;
import com.example.focustrackr.sensors.AppLocationManager;
import com.example.focustrackr.sensors.FocusSensorManager;

public class NewSessionActivity extends AppCompatActivity {

    private ActivityNewSessionBinding binding;
    private NewSessionViewModel viewModel;
    private FocusSensorManager focusSensorManager;
    private AppLocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NewSessionViewModel.class);

        focusSensorManager = new FocusSensorManager(this);
        focusSensorManager.setFocusListener(viewModel::updateFocus);

        locationManager = new AppLocationManager(this);

        setupObservers();

        binding.btnStartSession.setOnClickListener(v -> startSession());
        binding.btnEndSession.setOnClickListener(v -> endSession());
    }

    private void setupObservers() {

        // Observar el estado de la sesión
        viewModel.sessionState.observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case IDLE:
                    binding.chronometer.stop();
                    break;
                case RUNNING:
                    binding.chronometer.setBase(SystemClock.elapsedRealtime());
                    binding.chronometer.start();
                    break;
                case FINISHED:
                    finish();
                    break;
            }
        });

        // Observar el nivel de foco y actualizar UI en tiempo real
        viewModel.focusLevel.observe(this, focus -> {
            if (focus == null) return;
            binding.pbFocusLevel.setProgress((int) (float) focus);
            binding.tvFocusPercentage.setText("Foco: " + (int) (float) focus + "%");
        });
    }

    private boolean sessionStarted = false;

    private void startSession() {
        String name = binding.etSessionName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.etSessionName.setError(getString(R.string.new_session_need_name));
            return;
        }

        if (sessionStarted) {
            Toast.makeText(this, "La sesion ya esta en marcha", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionStarted = true;

        long startTime = SystemClock.elapsedRealtime();
        viewModel.startSession(startTime);

        focusSensorManager.startSafe();
        locationManager.requestLocation(viewModel::updateLocation);

        Toast.makeText(this, getString(R.string.new_session_started), Toast.LENGTH_SHORT).show();
    }

    private void endSession() {
        if (!sessionStarted) {
            Toast.makeText(this, "No hay sesion activa", Toast.LENGTH_SHORT).show();
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

}
