package com.example.focustrackr.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.AppDatabase;
import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.local.entity.SessionEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionRepository {

    private final SessionDao sessionDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SessionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.sessionDao = db.sessionDao();
    }

    public LiveData<List<SessionEntity>> getAllSessions() { return sessionDao.getAllSessions(); }
    public LiveData<SessionEntity> getSessionById(long id) { return sessionDao.getSessionById(id); }
    public LiveData<Integer> getTotalDuration() { return sessionDao.getTotalDuration(); }
    public LiveData<Integer> getTotalSessions() { return sessionDao.getTotalSessions(); }
    public LiveData<Float> getAvgFocus() { return sessionDao.getAvgFocus(); }

    public void insertSession(final SessionEntity entity) {
        executorService.execute(() -> sessionDao.insert(entity));
    }
}

