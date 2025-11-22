package com.example.focustrackr.ui.sessiondetail;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

/**
 * Actividad que muestra los detalles de una sesión seleccionada.
 * Permite visualizar información y eliminar la sesión.
 */
public class SessionDetailActivity extends AppCompatActivity {

    private SessionDetailViewModel viewModel;
    private TextView tvName, tvDuration, tvFocus, tvLocation, tvTimestamp;
    private MaterialButton btnDeleteSession;
    private SessionEntity currentSession; // Referencia para eliminar la sesión cargada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        // Referencia de componentes UI
        tvName = findViewById(R.id.tvDetailName);
        tvDuration = findViewById(R.id.tvDetailDuration);
        tvFocus = findViewById(R.id.tvDetailFocus);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvTimestamp = findViewById(R.id.tvDetailTimestamp);
        btnDeleteSession = findViewById(R.id.btnDeleteSession);

        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);

        // Recuperar ID de la sesión pasada desde el fragment
        long id = getIntent().getLongExtra("SESSION_ID", -1);
        if (id == -1) {
            finish();
            return;
        }

        // Carga y observación de datos de la sesión
        viewModel.getSessionById(id).observe(this, session -> {
            currentSession = session;
            bindSession(session);
        });

        /**
         * Listener para eliminar sesión.
         * Muestra confirmación antes de borrar.
         */
        btnDeleteSession.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar sesión")
                    .setMessage("¿Seguro que quieres eliminar esta sesión?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.deleteSession(currentSession);
                        Toast.makeText(this, "Sesión eliminada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    /**
     * Asigna los valores del objeto SessionEntity a los elementos visuales.
     */
    private void bindSession(SessionEntity session) {
        if (session == null) return;

        tvName.setText(session.getName());
        tvDuration.setText("Duración: " + session.getDurationMinutes() + " min");
        tvFocus.setText("Foco: " + (int) session.getFocusPercentage() + "%");
        tvLocation.setText("Lat: " + session.getLatitude() + ", Lon: " + session.getLongitude());

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(session.getTimestamp()));
        tvTimestamp.setText("Fecha: " + formattedDate);
    }
}
