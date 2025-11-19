package com.example.focustrackr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.data.repository.SessionRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;
    private final LiveData<List<SessionEntity>> sessions;
    private final LiveData<Integer> totalDuration;

    public MainViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
        sessions = sessionRepository.getAllSessions();
        totalDuration = sessionRepository.getTotalDuration();
    }

    public LiveData<List<SessionEntity>> getSessions() {
        return sessions;
    }

    public LiveData<Integer> getTotalDuration() {
        return totalDuration;
    }
}
