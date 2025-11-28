package com.example.focustrackr.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.focustrackr.data.local.entity.SessionEntity;

import java.util.List;

/**
 * DAO de acceso a datos de sesiones.
 * Proporciona operaciones CRUD y estadísticas básicas.
 */
@Dao
public interface SessionDao {

    /**
     * Inserta una nueva sesión y devuelve su ID.
     */
    @Insert
    long insert(SessionEntity session);

    /**
     * Obtiene todas las sesiones ordenadas por fecha (más reciente primero).
     */
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    LiveData<List<SessionEntity>> getAllSessions();

    /**
     * Busca una sesión específica por su ID.
     */
    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    LiveData<SessionEntity> getSessionById(long id);

    /**
     * Calcula la suma total de minutos registrados.
     */
    @Query("SELECT SUM(durationMinutes) FROM sessions")
    LiveData<Integer> getTotalDuration();

    /**
     * Devuelve la cantidad total de sesiones guardadas.
     */
    @Query("SELECT COUNT(*) FROM sessions")
    LiveData<Integer> getTotalSessions();

    /**
     * Calcula el promedio del porcentaje de enfoque del usuario.
     */
    @Query("SELECT AVG(focusPercentage) FROM sessions")
    LiveData<Float> getAvgFocus();

    /**
     * Elimina una sesión específica.
     */
    @Delete
    void delete(SessionEntity session);

    /**
     * Calcula la suma de minutos en un rango de fechas.
     * Utilizado para medir el progreso semanal.
     */
    @Query("SELECT SUM(durationMinutes) FROM sessions WHERE timestamp BETWEEN :startDate AND :endDate")
    LiveData<Integer> getDurationBetweenDates(long startDate, long endDate);

    /**
     * Obtiene los minutos agrupados por día.
     * Utilizado para calcular rachas consecutivas.
     */
    @Query("SELECT DATE(timestamp / 1000, 'unixepoch') AS day, SUM(durationMinutes) AS totalMinutes " +
            "FROM sessions " +
            "GROUP BY day " +
            "ORDER BY day DESC")
    LiveData<List<DayDuration>> getDailyDurations();

    /**
     * Obtiene las sesiones en un rango de fechas.
     * Útil para estadísticas adicionales de la semana.
     */
    @Query("SELECT * FROM sessions WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY durationMinutes DESC")
    LiveData<List<SessionEntity>> getSessionsBetweenDates(long startDate, long endDate);

    /**
     * Clase para representar la duración agrupada por día.
     */
    class DayDuration {
        public String day;
        public int totalMinutes;
    }
}
