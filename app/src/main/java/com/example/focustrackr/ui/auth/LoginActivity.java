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

/**
 * Pantalla de inicio de sesión.
 * Valida credenciales y guarda el usuario si el acceso es correcto.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si el usuario ya inició sesión, accede directamente a la pantalla principal.
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean(Constants.PREF_LOGGED_IN, false);
        if (loggedIn) {
            navigateToMain(prefs.getString(Constants.PREF_USER_EMAIL, ""));
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa el ViewModel para la lógica de validación.
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        /**
         * Listener del botón de inicio de sesión.
         * Envía email y contraseña al ViewModel.
         */
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString();
            loginViewModel.attemptLogin(email, password);
        });

        /**
         * Observa el resultado del login y muestra errores o continua en caso de éxito.
         */
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

    /**
     * Guarda el estado de usuario logueado y navega a la pantalla principal.
     */
    private void saveUserAndNavigate() {
        String email = binding.etEmail.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(Constants.PREF_LOGGED_IN, true)
                .putString(Constants.PREF_USER_EMAIL, email)
                .apply();

        navigateToMain(email);
    }

    /**
     * Cambia a MainActivity con transición de entrada/salida.
     */
    private void navigateToMain(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_USER_EMAIL, email);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
