package com.example.focustrackr.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad Room que representa una sesión de concentración.
 * Se almacena en la tabla "sessions".
 */
@Entity(tableName = "sessions")
public class SessionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id; // Identificador único de la sesión

    private String name; // Nombre o título de la sesión
    private int durationMinutes; // Duración total de la sesión (minutos)
    private float focusPercentage; // Porcentaje de enfoque alcanzado
    private double latitude; // Ubicación (latitud)
    private double longitude; // Ubicación (longitud)
    private long timestamp; // Fecha de creación (en milisegundos)
    private int distractionsCount; // Número de distracciones registradas

    /**
     * Constructor utilizado para la inserción en base de datos.
     */
    public SessionEntity(String name, int durationMinutes, float focusPercentage,
                         double latitude, double longitude, long timestamp, int distractionsCount) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.focusPercentage = focusPercentage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.distractionsCount = distractionsCount;
    }

    // Getters y setters requeridos por Room

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public float getFocusPercentage() {
        return focusPercentage;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getDistractionsCount() {
        return distractionsCount;
    }

    public void setDistractionsCount(int distractionsCount) {
        this.distractionsCount = distractionsCount;
    }
}
