package com.example.focustrackr.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.databinding.ActivityLoginBinding;
import com.example.focustrackr.ui.main.MainActivity;
import com.example.focustrackr.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si ya esta logueado, evitamos pasar por el login
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean(Constants.PREF_LOGGED_IN, false);
        if (loggedIn) {
            navigateToMain(prefs.getString(Constants.PREF_USER_EMAIL, ""));
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString();
            loginViewModel.attemptLogin(email, password);
        });

        loginViewModel.loginState.observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case INVALID_EMAIL:
                    binding.etEmail.setError(getString(R.string.login_email_error));
                    break;
                case INVALID_PASSWORD:
                    binding.etPassword.setError(getString(R.string.login_password_error));
                    break;
                case SUCCESS:
                    saveUserAndNavigate();
                    break;
            }
        });
    }

    private void saveUserAndNavigate() {
        String email = binding.etEmail.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(Constants.PREF_LOGGED_IN, true)
                .putString(Constants.PREF_USER_EMAIL, email)
                .apply();

        navigateToMain(email);
    }

    private void navigateToMain(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_USER_EMAIL, email);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
