package com.example.focustrackr.ui.auth;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * ViewModel encargado de validar las credenciales de inicio de sesión.
 */
public class LoginViewModel extends AndroidViewModel {

    /**
     * Estados posibles tras intentar realizar login.
     */
    public enum LoginState {
        SUCCESS,
        INVALID_EMAIL,
        INVALID_PASSWORD
    }

    // Estado observado por LoginActivity
    public MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Valida email y contraseña. Si son correctos devuelve SUCCESS.
     */
    public void attemptLogin(String email, String password) {
        // Validación de formato de email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginState.setValue(LoginState.INVALID_EMAIL);
            return;
        }

        // Validación de contraseña (mínimo 6 caracteres)
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            loginState.setValue(LoginState.INVALID_PASSWORD);
            return;
        }

        // Validación correcta
        loginState.setValue(LoginState.SUCCESS);
    }
}
