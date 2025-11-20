package com.example.focustrackr.ui.sessiondetail;

import android.os.Bundle;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionDetailActivity extends AppCompatActivity {

    private SessionDetailViewModel viewModel;
    private TextView tvName, tvDuration, tvFocus, tvLocation, tvTimestamp, tvDistractions;
    private ProgressBar pbFocus;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        // Referencias UI
        tvName = findViewById(R.id.tvDetailName);
        tvDuration = findViewById(R.id.tvDetailDuration);
        tvFocus = findViewById(R.id.tvDetailFocus);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvTimestamp = findViewById(R.id.tvDetailTimestamp);
        tvDistractions = findViewById(R.id.tvDetailDistractions); // NUEVO
        pbFocus = findViewById(R.id.pbDetailFocus); // NUEVO

        viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);

        long id = getIntent().getLongExtra("SESSION_ID", -1);
        if (id == -1) {
            finish();
            return;
        }

        viewModel.getSessionById(id).observe(this, this::bindSession);
    }

    private void bindSession(SessionEntity session) {
        if (session == null) return;

        tvName.setText(session.getName());
        tvDuration.setText("Duracion: " + session.getDurationMinutes() + " min");
        tvFocus.setText("Foco: " + (int) session.getFocusPercentage() + "%");
        tvLocation.setText("Lat: " + session.getLatitude() + " | Lon: " + session.getLongitude());
        tvTimestamp.setText("Fecha: " + sdf.format(new Date(session.getTimestamp())));

        // Animación barra de foco
        pbFocus.setProgress(0);
        pbFocus.animate()
                .setDuration(700)
                .withStartAction(() -> pbFocus.setProgress((int) session.getFocusPercentage()))
                .start();

        // Distracciones (si las tienes, debes haber agregado el campo antes)
        // Por ahora, asumo que existe método getDistractionsCount()
        int distractions = 0; // Sustituir si añades el campo: session.getDistractionsCount();

        tvDistractions.setText("Distracciones detectadas: " + distractions);

        if (distractions > 0) {
            tvDistractions.setTextColor(getColor(R.color.focus_error));
            vibrateShort();
        }
    }

    private void vibrateShort() {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(
                            120, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(120);
                }
            }
        } catch (Exception ignored) { }
    }
}
