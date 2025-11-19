package com.example.focustrackr.ui.sessiondetail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;

public class SessionDetailActivity extends AppCompatActivity {

    private SessionDetailViewModel viewModel;
    private TextView tvName, tvDuration, tvFocus, tvLocation, tvTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        tvName = findViewById(R.id.tvDetailName);
        tvDuration = findViewById(R.id.tvDetailDuration);
        tvFocus = findViewById(R.id.tvDetailFocus);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvTimestamp = findViewById(R.id.tvDetailTimestamp);

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
        tvLocation.setText("Lat: " + session.getLatitude() + ", Lon: " + session.getLongitude());
        tvTimestamp.setText("Ts: " + session.getTimestamp());
    }
}
