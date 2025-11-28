package com.example.focustrackr.ui.main;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * ViewModel responsable de los datos de objetivos.
 */
public class GoalsViewModel extends AndroidViewModel {

    private final SharedPreferences prefs;

    private final MutableLiveData<Integer> weeklyGoal;
    private final MutableLiveData<Integer> weeklyProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> currentStreak = new MutableLiveData<>(0);

    public GoalsViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences(Constants.PREFS_NAME, 0);
        int savedGoal = prefs.getInt(Constants.PREF_WEEKLY_GOAL, 300);
        weeklyGoal = new MutableLiveData<>(savedGoal);
    }

    public LiveData<Integer> getWeeklyGoal() { return weeklyGoal; }
    public LiveData<Integer> getWeeklyProgress() { return weeklyProgress; }
    public LiveData<Integer> getCurrentStreak() { return currentStreak; }

    /**
     * Establece un nuevo objetivo semanal y lo guarda.
     */
    public void setWeeklyGoal(int minutes) {
        prefs.edit().putInt(Constants.PREF_WEEKLY_GOAL, minutes).apply();
        weeklyGoal.setValue(minutes);
    }

    /**
     * Actualiza el progreso semanal actual.
     */
    public void setWeeklyProgress(int minutes) {
        weeklyProgress.setValue(minutes);
    }

    /**
     * Actualiza la racha actual.
     */
    public void setCurrentStreak(int days) {
        currentStreak.setValue(days);
    }

    /**
     * Vincula datos del MainViewModel para obtener progreso real y racha.
     */
    public void connectMainViewModel(MainViewModel mainViewModel) {
        mainViewModel.getWeeklyDuration().observeForever(progress ->
                setWeeklyProgress(progress != null ? progress : 0));

        mainViewModel.getDailyDurations().observeForever(this::calculateStreak);
    }

    /**
     * Calcula la racha de días consecutivos con sesiones.
     */
    public void calculateStreak(List<SessionDao.DayDuration> dayDurations) {
        if (dayDurations == null || dayDurations.isEmpty()) {
            setCurrentStreak(0);
            return;
        }

        Set<String> daysWithSessions = new HashSet<>();
        for (SessionDao.DayDuration dayDuration : dayDurations) {
            if (dayDuration != null && dayDuration.totalMinutes > 0 && dayDuration.day != null) {
                daysWithSessions.add(dayDuration.day);
            }
        }

        if (daysWithSessions.isEmpty()) {
            setCurrentStreak(0);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        int streak = 0;
        while (true) {
            String currentDay = sdf.format(calendar.getTime());
            if (daysWithSessions.contains(currentDay)) {
                streak++;
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                break;
            }
        }

        setCurrentStreak(streak);
    }

    /**
     * Fuerza un recálculo explícito de la racha.
     */
    public void forceRecalculateStreak(List<SessionDao.DayDuration> dailyData) {
        calculateStreak(dailyData);
    }
}
