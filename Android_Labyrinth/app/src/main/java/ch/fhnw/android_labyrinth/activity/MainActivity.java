package ch.fhnw.android_labyrinth.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import ch.fhnw.android_labyrinth.LabyrinthRegistry;
import ch.fhnw.android_labyrinth.R;
import ch.fhnw.android_labyrinth.view.ClickView_horizontal;
import ch.fhnw.android_labyrinth.view.ClickView_vertical;
import oscP5.OscMessage;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final long TIMEOUT = 20;

    private long lastSent;
    private ClickView_vertical clickViewVertical;
    private ClickView_horizontal clickViewHorizontal;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        clickViewVertical = (ClickView_vertical) findViewById(R.id.clickview_vertical);
        clickViewVertical.setDisplayMetrics(displayMetrics);

        clickViewHorizontal = (ClickView_horizontal) findViewById(R.id.clickview_horizontal);
       clickViewHorizontal.setDisplayMetrics(displayMetrics);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  clickViewVertical.enableSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //clickViewVertical.disableSensor();
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
