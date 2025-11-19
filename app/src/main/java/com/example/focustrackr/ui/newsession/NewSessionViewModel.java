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

    private long startTimeMillis;

    public NewSessionViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
    }

    public void startSession(long baseTime) {
        startTimeMillis = System.currentTimeMillis();
        chronometerBase.setValue(baseTime);
        sessionState.setValue(SessionState.RUNNING);
    }

    public void updateFocus(float focus) {
        focusLevel.postValue(focus);
    }

    public void updateLocation(double lat, double lon) {
        latitude.postValue(lat);
        longitude.postValue(lon);
    }

    public boolean endSession(String name, int targetMinutes) {
        long endTime = System.currentTimeMillis();
        int durationMinutes = (int) ((endTime - startTimeMillis) / 60000);

        if (durationMinutes < 1) return false;

        SessionEntity entity = new SessionEntity(
                name,
                durationMinutes,
                focusLevel.getValue() != null ? focusLevel.getValue() : 0f,
                latitude.getValue() != null ? latitude.getValue() : 0,
                longitude.getValue() != null ? longitude.getValue() : 0,
                endTime
        );

        sessionRepository.insertSession(entity);
        sessionState.setValue(SessionState.FINISHED);
        return true;
    }
}
