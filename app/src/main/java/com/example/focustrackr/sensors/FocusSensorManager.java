package com.example.focustrackr.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class FocusSensorManager implements SensorEventListener {

    public interface FocusListener {
        void onFocusLevelChanged(float focusPercentage);
    }

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private FocusListener listener;

    private boolean isRunning = false;
    private float lastFocus = 100f;

    // Contador de distracciones
    private int distractionCount = 0;
    private long lastDistractionTs = 0;

    // Control de estabilidad (para no evaluar en cada frame)
    private long lastUpdateTs = 0;
    private static final long UPDATE_INTERVAL_MS = 300; // 3 lecturas máximo por segundo

    public FocusSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
    }

    public void setFocusListener(FocusListener listener) {
        this.listener = listener;
    }

    public void startSafe() {
        if (isRunning || accelerometer == null) return;
        isRunning = true;
        lastFocus = 100f;
        distractionCount = 0;
        lastUpdateTs = 0;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

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

        float x = Math.abs(event.values[0]);
        float y = Math.abs(event.values[1]);
        float z = Math.abs(event.values[2]);

        // Cálculo bruto de movimiento
        float movement = x + y + z;

        // Ajustes de calibración
        float movementThreshold = 10f;     // movimiento que empieza a afectar
        float maxMovementEffect = 30f;     // movimiento muy bruto (correr, agitar)

        float normalizedMovement = Math.min(Math.max(movement - movementThreshold, 0), maxMovementEffect);

        // Penalización progresiva (hasta un máximo de -50%)
        float movementPenalty = (normalizedMovement / maxMovementEffect) * 50f;

        // Foco final
        float newFocus = 100f - movementPenalty;

        // Suavizamos el valor para no generar picos bruscos
        float smoothFactor = 0.15f;
        lastFocus = lastFocus * (1 - smoothFactor) + newFocus * smoothFactor;

        // Detección de distracción → movimiento fuerte
        if (movement > (movementThreshold + 8)) {
            if (now - lastDistractionTs > 1200) { // al menos 1.2s entre distracciones
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

    public int getDistractionCount() {
        return distractionCount;
    }
}
