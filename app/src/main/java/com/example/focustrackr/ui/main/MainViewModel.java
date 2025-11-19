package com.example.focustrackr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.data.repository.SessionRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;
    private final LiveData<List<SessionEntity>> sessions;
    private final LiveData<Integer> totalDuration;
    private final LiveData<Integer> totalSessions;
    private final LiveData<Float> avgFocus;

    public MainViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
        sessions = sessionRepository.getAllSessions();
        totalDuration = sessionRepository.getTotalDuration();
        totalSessions = sessionRepository.getTotalSessions();
        avgFocus = sessionRepository.getAvgFocus();
    }

    public LiveData<List<SessionEntity>> getSessions() { return sessions; }
    public LiveData<Integer> getTotalDuration() { return totalDuration; }
    public LiveData<Integer> getTotalSessions() { return totalSessions; }
    public LiveData<Float> getAvgFocus() { return avgFocus; }
}

