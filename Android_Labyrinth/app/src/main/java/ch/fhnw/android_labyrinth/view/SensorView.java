package ch.fhnw.android_labyrinth.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import ch.fhnw.android_labyrinth.activity.MainActivity;

public class SensorView extends View implements SensorEventListener {

    private static final String TAG = "SensorView";
    private final Context context;
    private final WindowManager mWindowManager;

    private Paint paint;

    private float x_factor = 1;
    private float y_factor = 1;

    private boolean lineDrawEnabled = false;
    private float clickPosX = 0;
    private float clickPosY = 0;

    private float mPitch = 0; // Degrees
    private float mRoll = 0; // Degrees, left roll is positive

    private int mLastAccuracy;

    private DisplayMetrics displayMetrics;


    public SensorView(final Context context) {
        super(context);
        this.context = context;

        mWindowManager = ((Activity)context).getWindow().getWindowManager();

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.YELLOW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set background color
        canvas.drawColor(Color.DKGRAY);

        // Draw a small circle in the middle
        paint.setColor(Color.WHITE);
        canvas.drawCircle(displayMetrics.widthPixels/2f, displayMetrics.heightPixels/2f, 10, paint);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(displayMetrics.widthPixels/2f, displayMetrics.heightPixels/2f, 9, paint);
        paint.setColor(Color.YELLOW);

        // Draw a line to the selected point
        if (lineDrawEnabled) {
            canvas.drawLine(displayMetrics.widthPixels/2f, displayMetrics.heightPixels/2f, clickPosX, clickPosY, paint);
            canvas.drawCircle(clickPosX, clickPosY, 8, paint);
            canvas.drawText("(" + clickPosX + "/" + clickPosY + ")", 8, 13, paint);
            canvas.drawText("(" + (int)(clickPosX /x_factor) + "/" + (int)(clickPosY /y_factor) + ")", 8, 26, paint);
        }
    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
        calculateDisplaySize();
    }

    private void calculateDisplaySize() {
        this.x_factor = displayMetrics.widthPixels / 180f;
        this.y_factor = displayMetrics.heightPixels / 180f;

        Log.d(TAG, "Display width in px is " + displayMetrics.widthPixels);
        Log.d(TAG, "Display height in px is " + displayMetrics.heightPixels);
    }

    private void setXYParams(float x, float y) {
        lineDrawEnabled = true;

        this.clickPosX = x;
        this.clickPosY = y;

        ((MainActivity)getContext()).moveTo((int)(clickPosX /x_factor), (int)(clickPosY /y_factor));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            updatePosition(event.values);
        }
    }

    private void updatePosition(float[] rotationVector) {

        float[] rotationMatrix = new float[9];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;

        switch (mWindowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float pitch = orientation[1] * -57;
        float roll = orientation[2] * -57;

        Log.d(TAG, "Pitch: " + pitch);
        Log.d(TAG, "Roll:  " + roll);
        Log.d(TAG, "-----");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }

    public void disableSensor() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    public void enableSensor() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d(TAG, "Available Sensors: ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            for (Sensor s : sensorList) {
                Log.d(TAG, s.getStringType());
            }
        } else {
            for (Sensor s : sensorList) {
                Log.d(TAG, "Sensor:" + s.getType());
            }
        }

        boolean registered = sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Sensor registered: " + registered);
    }
}
