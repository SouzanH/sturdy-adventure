package ch.fhnw.android_labyrinth.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import ch.fhnw.android_labyrinth.LabyrinthRegistry;
import ch.fhnw.android_labyrinth.view.SensorView;
import oscP5.OscMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final long TIMEOUT = 200;

    private long lastSent;
    private SensorView view;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view = new SensorView(this);
        view.setDisplayMetrics(displayMetrics);
        setContentView(view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        view.enableSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.disableSensor();
    }

    public void moveTo(int x, int y) {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSent > TIMEOUT ){
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

            Log.d(TAG, "Sending messages to server");
            LabyrinthRegistry.oscP5.send(oscMessage);
            lastSent = currentTime;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
