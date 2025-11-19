package com.example.focustrackr.ui.auth;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

    public enum LoginState {
        SUCCESS,
        INVALID_EMAIL,
        INVALID_PASSWORD
    }

    public MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void attemptLogin(String email, String password) {

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginState.setValue(LoginState.INVALID_EMAIL);
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            loginState.setValue(LoginState.INVALID_PASSWORD);
            return;
        }

        loginState.setValue(LoginState.SUCCESS);
    }
}
