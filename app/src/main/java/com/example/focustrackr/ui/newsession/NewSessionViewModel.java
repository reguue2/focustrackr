package com.example.focustrackr.ui.newsession;

import android.app.Application;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.data.repository.SessionRepository;

public class NewSessionViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;

    public MutableLiveData<SessionState> sessionState = new MutableLiveData<>(SessionState.IDLE);
    public MutableLiveData<Float> focusLevel = new MutableLiveData<>(0f);
    public MutableLiveData<Double> latitude = new MutableLiveData<>(0.0);
    public MutableLiveData<Double> longitude = new MutableLiveData<>(0.0);
    public MutableLiveData<Long> chronometerBase = new MutableLiveData<>(0L);

    private long sessionStartTime = -1;
    private boolean sessionActive = false;

    public NewSessionViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
    }

    /**
     * Inicia la sesión si no había ninguna activa.
     */
    public void startSession(long baseTime) {
        if (sessionActive) return; // Evita múltiples sesiones

        sessionStartTime = System.currentTimeMillis();
        chronometerBase.setValue(baseTime);
        sessionActive = true;
        sessionState.setValue(SessionState.RUNNING);
    }

    /**
     * Recibe los datos de foco del sensor
     */
    public void updateFocus(float focus) {
        focusLevel.postValue(Math.min(100f, Math.max(0f, focus)));
    }

    public void updateLocation(double lat, double lon) {
        latitude.postValue(lat);
        longitude.postValue(lon);
    }

    /**
     * Finaliza sesión con validaciones coherentes.
     */
    public boolean endSession(String name, int targetMinutes) {
        if (!sessionActive || sessionStartTime < 0) return false; // No hay sesión activa

        long now = System.currentTimeMillis();
        int durationMinutes = Math.max(1, (int) ((now - sessionStartTime) / 60000));

        // Validación básica de nombre (profesional)
        if (name == null || name.trim().isEmpty()) return false;

        float finalFocus = focusLevel.getValue() != null ? focusLevel.getValue() : 0f;
        double lat = latitude.getValue() != null ? latitude.getValue() : 0;
        double lon = longitude.getValue() != null ? longitude.getValue() : 0;

        SessionEntity entity = new SessionEntity(
                name.trim(),
                durationMinutes,
                finalFocus,
                lat,
                lon,
                now
        );

        sessionRepository.insertSession(entity);

        sessionActive = false;
        sessionState.setValue(SessionState.FINISHED);
        resetState();

        return true;
    }

    /**
     * Permite reiniciar estados al acabar.
     */
    private void resetState() {
        sessionStartTime = -1;
        focusLevel.setValue(0f);
        latitude.setValue(0.0);
        longitude.setValue(0.0);
        chronometerBase.setValue(0L);
    }
}
