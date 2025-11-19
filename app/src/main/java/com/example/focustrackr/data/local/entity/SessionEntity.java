package com.example.focustrackr.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class SessionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private int durationMinutes;
    private float focusPercentage;
    private double latitude;
    private double longitude;
    private long timestamp;

    public SessionEntity(String name, int durationMinutes, float focusPercentage,
                         double latitude, double longitude, long timestamp) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.focusPercentage = focusPercentage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

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
}
