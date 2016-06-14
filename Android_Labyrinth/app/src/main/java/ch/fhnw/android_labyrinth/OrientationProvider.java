package ch.fhnw.android_labyrinth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import ch.fhnw.android_labyrinth.activity.MainActivity;

public class OrientationProvider implements SensorEventListener {

    private static OrientationProvider provider;
    private SensorManager sensorManager;
    private Boolean supported;
    private int mLastAccuracy;
    private boolean running;


//    /**
//     * Rotation Matrix
//     */
//    private final float[] MAG = new float[]{1f, 1f, 1f};
//    private final float[] I = new float[16];
//    private final float[] R = new float[16];
//    private final float[] outR = new float[16];
//    private final float[] LOC = new float[3];
    private OrientationListener listener;
    private Sensor sensor;


    public boolean isSupported() {
        if (supported == null) {
            if (MainActivity.getContext() != null) {
                sensorManager = (SensorManager) MainActivity.getContext().getSystemService(Context.SENSOR_SERVICE);
                boolean supported = true;
                for (int sensorType : getRequiredSensors()) {
                    List<Sensor> sensors = sensorManager.getSensorList(sensorType);
                    supported = (sensors.size() > 0);
                }
                this.supported = supported;
                return supported;
            }
        }
        return supported;
    }

    public static OrientationProvider getInstance() {
        if (provider == null) {
            provider = new OrientationProvider();
        }
        return provider;
    }


    private List<Integer> getRequiredSensors() {
        return Arrays.asList(
                Sensor.TYPE_ACCELEROMETER
        );
    }


    public void start(OrientationListener listener) {

        this.listener = listener;

        for (int sensorType : getRequiredSensors()) {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor s : sensors)
            Log.d("Available Sensor: ", s.getName());
            if (sensors.size() > 0) {
                sensor = sensors.get(0);
                running = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL) && running;
            }
        }
    }

    public void stop() {
        running = false;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        SensorManager.getRotationMatrix(R, I, event.values, MAG);
//
//
//        // TODO correct orientation
//        SensorManager.remapCoordinateSystem(
//                R,
//                SensorManager.AXIS_X,
//                SensorManager.AXIS_Y,
//                outR);
//
//        SensorManager.getOrientation(outR, LOC);

        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];


        listener.onOrientationChanged(pitch_angle, roll_angle);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }
}
