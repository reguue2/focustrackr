package com.example.focustrackr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.repository.SessionRepository;

import java.util.List;

/**
 * ViewModel para estadísticas basadas en ubicación.
 */
public class LocationViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;
    private final LiveData<List<SessionDao.LocationStats>> locationStats;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
        locationStats = sessionRepository.getLocationStats();
    }

    public LiveData<List<SessionDao.LocationStats>> getLocationStats() { return locationStats; }
}
