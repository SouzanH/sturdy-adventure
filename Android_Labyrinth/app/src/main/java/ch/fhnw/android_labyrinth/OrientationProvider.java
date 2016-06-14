package ch.fhnw.android_labyrinth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ch.fhnw.android_labyrinth.activity.MainActivity;

public class OrientationProvider implements SensorEventListener {

    private static OrientationProvider provider;
    private SensorManager sensorManager;
    private int mLastAccuracy;

    private OrientationListener listener;
    private Sensor sensor;
    private float[] mGravity;
    private float[] mGeomagnetic;


    public static OrientationProvider getInstance() {
        if (provider == null) {
            provider = new OrientationProvider();
        }
        return provider;
    }



    public void start(OrientationListener listener) {

        sensorManager = (SensorManager) MainActivity.getContext().getSystemService(Context.SENSOR_SERVICE);

        Sensor accell = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accell, SensorManager.SENSOR_DELAY_NORMAL);

        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);

        this.listener = listener;
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                float pitch = - orientation[1];
                float roll = orientation[2];

                listener.onOrientationChanged(roll, pitch);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }
}
