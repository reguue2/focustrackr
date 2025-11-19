package com.example.focustrackr.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class FocusSensorManager implements SensorEventListener {

    public interface FocusListener {
        void onFocusLevelChanged(float focusPercentage);
    }

    private final SensorManager sensorManager;
    private final Sensor accelerometer;

    private FocusListener listener;

    private long sessionStartTime = 0;
    private long focusedTime = 0;
    private boolean isRunning = false;

    private float lastX, lastY, lastZ;
    private boolean firstEvent = true;

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
        sessionStartTime = System.currentTimeMillis();
        focusedTime = 0;
        firstEvent = true;

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

        if (firstEvent) {
            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];
            firstEvent = false;
            return;
        }

        float dx = Math.abs(event.values[0] - lastX);
        float dy = Math.abs(event.values[1] - lastY);
        float dz = Math.abs(event.values[2] - lastZ);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        long now = System.currentTimeMillis();
        long elapsedTotal = now - sessionStartTime;

        if (elapsedTotal <= 0) return;

        float threshold = 0.5f;

        // Si hay poco movimiento, consideramos que sigue enfocado
        if (dx < threshold && dy < threshold && dz < threshold) {
            focusedTime += 100;
        }

        // Limitar % y evitar NaN
        float focusPercentage = Math.min((focusedTime * 100f) / elapsedTotal, 100f);

        if (listener != null) {
            listener.onFocusLevelChanged(focusPercentage);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar lógica aquí para MVP
    }
}
