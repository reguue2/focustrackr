package com.example.focustrackr.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.AppDatabase;
import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.local.entity.SessionEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio para gestionar el acceso a datos de sesiones.
 * Centraliza las operaciones con Room y mantiene la lógica de acceso en segundo plano.
 */
public class SessionRepository {

    private final SessionDao sessionDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Obtiene la instancia del DAO desde la base de datos.
     */
    public SessionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.sessionDao = db.sessionDao();
    }

    /**
     * Recupera todas las sesiones almacenadas.
     */
    public LiveData<List<SessionEntity>> getAllSessions() {
        return sessionDao.getAllSessions();
    }

    /**
     * Recupera una sesión concreta por su ID.
     */
    public LiveData<SessionEntity> getSessionById(long id) {
        return sessionDao.getSessionById(id);
    }

    /**
     * Obtiene minutos totales registrados.
     */
    public LiveData<Integer> getTotalDuration() {
        return sessionDao.getTotalDuration();
    }

    /**
     * Devuelve la cantidad de sesiones guardadas.
     */
    public LiveData<Integer> getTotalSessions() {
        return sessionDao.getTotalSessions();
    }

    /**
     * Calcula el porcentaje medio de enfoque.
     */
    public LiveData<Float> getAvgFocus() {
        return sessionDao.getAvgFocus();
    }

    /**
     * Inserta una nueva sesión en segundo plano.
     */
    public void insertSession(final SessionEntity entity) {
        executorService.execute(() -> sessionDao.insert(entity));
    }

    /**
     * Elimina una sesión en segundo plano.
     */
    public void deleteSession(SessionEntity session) {
        executorService.execute(() -> sessionDao.delete(session));
    }

    /**
     * Obtiene los minutos registradas entre dos fechas.
     */
    public LiveData<Integer> getDurationBetweenDates(long startDate, long endDate) {
        return sessionDao.getDurationBetweenDates(startDate, endDate);
    }

    /**
     * Obtiene los datos diarios (para calcular rachas).
     */
    public LiveData<List<SessionDao.DayDuration>> getDailyDurations() {
        return sessionDao.getDailyDurations();
    }

    /**
     * Obtiene estadísticas de ubicación.
     */
    public LiveData<List<SessionDao.LocationStats>> getLocationStats() {
        return sessionDao.getLocationStats();
    }


}
