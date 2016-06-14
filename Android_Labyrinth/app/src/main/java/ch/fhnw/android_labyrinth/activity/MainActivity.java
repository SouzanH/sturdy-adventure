package ch.fhnw.android_labyrinth.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import ch.fhnw.android_labyrinth.LabyrinthRegistry;
import ch.fhnw.android_labyrinth.OrientationListener;
import ch.fhnw.android_labyrinth.OrientationProvider;
import ch.fhnw.android_labyrinth.view.SensorView;
import oscP5.OscMessage;

public class MainActivity extends Activity implements OrientationListener {

    private static final String TAG = "MainActivity";
    private static final long TIMEOUT = 1000;

    private static MainActivity CONTEXT;

    private long lastSent;
    private SensorView sensorView;
    private float pitchMin;
    private float pitchMax;
    private float rollMin;
    private float rollMax;

    public static Context getContext() {
        return CONTEXT;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CONTEXT = this;

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        sensorView = new SensorView(this);
        sensorView.setDisplayMetrics(displayMetrics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OrientationProvider orientationProvider = OrientationProvider.getInstance();

        orientationProvider.start(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        OrientationProvider.getInstance().stop();

    }

    public void moveTo(int x, int y) {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSent > TIMEOUT) {
            OscMessage oscMessage = new OscMessage("Lab");

            // Limit values to maximum allowed
            if (x < 0) x = 0;
            if (x > 180) x = 180;
            if (y < 0) y = 0;
            if (y > 180) y = 180;

            oscMessage.add(0);
            oscMessage.add(x);
            oscMessage.add(1);
            oscMessage.add(y);

            Log.d(TAG, "(x, y): (" + x + "," + y + ")");

            Log.d(TAG, "Sending messages to server");
            if (LabyrinthRegistry.oscP5 != null) {
                LabyrinthRegistry.oscP5.send(oscMessage);
            } else {
                Log.d(TAG, "No connection to oscP5");
            }
            lastSent = currentTime;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

        int pitchInt = (int) (pitch * 90f + 90);
        int rollInt = (int) (roll * 90f + 90);

        moveTo(pitchInt, rollInt);
        sensorView.setXYParams(pitchInt, rollInt);

        if (pitch < pitchMin) {
            pitchMin = pitch;
            Log.d(TAG, "PitchMin: " + pitchMin);
        }
        if (pitch > pitchMax) {
            pitchMax = pitch;
            Log.d(TAG, "PitchMax: " + pitchMax);
        }
        if (roll < rollMin) {
            rollMin = roll;
            Log.d(TAG, "RollMin: " + rollMin);
        }
        if (roll > rollMax) {
            rollMax = roll;
            Log.d(TAG, "RollMax: " + rollMax);
        }

    }
}
