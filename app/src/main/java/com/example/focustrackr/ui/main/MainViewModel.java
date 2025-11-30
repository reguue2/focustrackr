package com.example.focustrackr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.data.repository.SessionRepository;

import java.util.Calendar;
import java.util.List;

/**
 * ViewModel de la actividad principal.
 * Proporciona acceso a estadísticas y lista de sesiones desde el repositorio.
 */
public class MainViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;
    private final LiveData<List<SessionEntity>> sessions;
    private final LiveData<Integer> totalDuration;
    private final LiveData<Integer> totalSessions;
    private final LiveData<Float> avgFocus;
    private final LiveData<Integer> weeklyDuration;
    private final LiveData<List<SessionDao.DayDuration>> dailyDurations;

    public MainViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);

        // Datos observables proporcionados a la UI.
        sessions = sessionRepository.getAllSessions();
        totalDuration = sessionRepository.getTotalDuration();
        totalSessions = sessionRepository.getTotalSessions();
        avgFocus = sessionRepository.getAvgFocus();

        // Calcula progreso semanal desde el LUNES real hasta ahora
        Calendar calendar = Calendar.getInstance();

        // Definir que la semana empieza el LUNES
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        // Resetear hora para inicio del día
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Ajustar al LUNES de la semana actual
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        weeklyDuration = sessionRepository.getDurationBetweenDates(start, end);


        // Datos diarios para cálculo de racha.
        dailyDurations = sessionRepository.getDailyDurations();
    }

    public LiveData<List<SessionEntity>> getSessions() { return sessions; }
    public LiveData<Integer> getTotalDuration() { return totalDuration; }
    public LiveData<Integer> getTotalSessions() { return totalSessions; }
    public LiveData<Float> getAvgFocus() { return avgFocus; }
    public LiveData<Integer> getWeeklyDuration() { return weeklyDuration; }
    public LiveData<List<SessionDao.DayDuration>> getDailyDurations() { return dailyDurations; }
}
