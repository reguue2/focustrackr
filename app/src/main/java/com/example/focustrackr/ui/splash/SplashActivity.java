package com.example.focustrackr.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focustrackr.R;
import com.example.focustrackr.ui.auth.LoginActivity;
import com.example.focustrackr.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean loggedIn = prefs.getBoolean("logged_in", false);
            String email = prefs.getString("user_email", null);

            Intent intent;
            if (loggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("USER_EMAIL", email);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
