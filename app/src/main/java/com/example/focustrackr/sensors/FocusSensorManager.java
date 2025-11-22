package com.example.focustrackr.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Gestiona la detección del nivel de enfoque utilizando el acelerómetro.
 * Calcula un porcentaje de foco y contabiliza distracciones por movimientos bruscos.
 */
public class FocusSensorManager implements SensorEventListener {

    /**
     * Listener para notificar cambios en el nivel de enfoque.
     */
    public interface FocusListener {
        void onFocusLevelChanged(float focusPercentage);
    }

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private FocusListener listener;

    private boolean isRunning = false;
    private float lastFocus = 100f;

    // Contador de distracciones detectadas
    private int distractionCount = 0;
    private long lastDistractionTs = 0;

    // Intervalo mínimo entre actualizaciones para evitar lecturas excesivas
    private long lastUpdateTs = 0;
    private static final long UPDATE_INTERVAL_MS = 300;

    public FocusSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
    }

    /**
     * Asigna el listener que recibirá los cambios de foco.
     */
    public void setFocusListener(FocusListener listener) {
        this.listener = listener;
    }

    /**
     * Inicia las lecturas del acelerómetro.
     */
    public void startSafe() {
        if (isRunning || accelerometer == null) return;
        isRunning = true;
        lastFocus = 100f;
        distractionCount = 0;
        lastUpdateTs = 0;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Detiene la lectura del sensor.
     */
    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isRunning || event == null) return;

        long now = System.currentTimeMillis();
        if (lastUpdateTs != 0 && now - lastUpdateTs < UPDATE_INTERVAL_MS) return;
        lastUpdateTs = now;

        // Datos brutos del acelerómetro
        float x = Math.abs(event.values[0]);
        float y = Math.abs(event.values[1]);
        float z = Math.abs(event.values[2]);

        float movement = x + y + z;
        float movementThreshold = 10f;
        float maxMovementEffect = 30f;

        float normalizedMovement = Math.min(Math.max(movement - movementThreshold, 0), maxMovementEffect);
        float movementPenalty = (normalizedMovement / maxMovementEffect) * 50f;

        // Cálculo del foco (penalizando movimientos fuertes)
        float newFocus = 100f - movementPenalty;

        // Suavizado para evitar cambios bruscos repentinos
        float smoothFactor = 0.15f;
        lastFocus = lastFocus * (1 - smoothFactor) + newFocus * smoothFactor;

        // Contabilización de distracciones
        if (movement > (movementThreshold + 8)) {
            if (now - lastDistractionTs > 1200) {
                distractionCount++;
                lastDistractionTs = now;
            }
        }

        if (listener != null) {
            listener.onFocusLevelChanged(Math.max(0f, Math.min(100f, lastFocus)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Devuelve el número de distracciones detectadas.
     */
    public int getDistractionCount() {
        return distractionCount;
    }
}
