package com.example.focustrackr.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focustrackr.R;
import com.example.focustrackr.ui.auth.LoginActivity;
import com.example.focustrackr.ui.main.MainActivity;
import com.example.focustrackr.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1700; // ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.imgLogo);

        // Animación sutil
        logo.setScaleX(0.8f);
        logo.setScaleY(0.8f);
        logo.setAlpha(0f);
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(800).start();

        // Decidir a qué pantalla ir
        new Handler().postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean(Constants.PREF_LOGGED_IN, false);

            if (isLoggedIn) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        }, SPLASH_DURATION);
    }
}
