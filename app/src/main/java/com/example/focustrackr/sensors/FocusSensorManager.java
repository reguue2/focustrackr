package com.example.focustrackr.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class FocusSensorManager implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;

    public interface FocusListener {
        void onFocusLevelChanged(float focusPercentage);
    }

    private FocusListener listener;

    private long startTime;
    private long focusedTime;
    private float lastX, lastY, lastZ;
    private boolean firstEvent = true;

    public FocusSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setFocusListener(FocusListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (accelerometer != null) {
            startTime = System.currentTimeMillis();
            focusedTime = 0;
            firstEvent = true;
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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
        long delta = now - startTime;

        if (delta <= 0) return;

        float threshold = 0.5f;
        if (dx < threshold && dy < threshold && dz < threshold) {
            focusedTime += 100;
        }

        float focusPercentage = (focusedTime * 100f) / (float) delta;

        if (listener != null) {
            listener.onFocusLevelChanged(focusPercentage);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
