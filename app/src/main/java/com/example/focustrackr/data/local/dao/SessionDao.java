package com.example.focustrackr.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.focustrackr.data.local.entity.SessionEntity;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    long insert(SessionEntity session);

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    LiveData<List<SessionEntity>> getAllSessions();

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    LiveData<SessionEntity> getSessionById(long id);

    @Query("SELECT SUM(durationMinutes) FROM sessions")
    LiveData<Integer> getTotalDuration();
}
