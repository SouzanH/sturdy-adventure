package ch.fhnw.android_labyrinth.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SensorView extends View {

    private static final String TAG = "SensorView";
    private final Context context;
    private final WindowManager mWindowManager;

    private Paint paint;
    private final Paint colorChangeOrientation;

    private float x_factor = 1;
    private float y_factor = 1;

    private boolean lineDrawEnabled = false;
    private float clickPosX = 90;
    private float clickPosY = 90;

    private final float buttonHeight = 0.85f;

    private DisplayMetrics displayMetrics;


    public SensorView(final Context context) {
        super(context);
        this.context = context;

        mWindowManager = ((Activity)context).getWindow().getWindowManager();

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.YELLOW);

        colorChangeOrientation = new Paint();
        colorChangeOrientation.setColor(Color.RED);

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
            final float stepSizeWidth = displayMetrics.widthPixels / 180;
            final float stepSizeHeight = displayMetrics.heightPixels / 180;

            canvas.drawLine(displayMetrics.widthPixels/2f, displayMetrics.heightPixels/2f, clickPosX * stepSizeWidth, clickPosY * stepSizeHeight, paint);

            canvas.drawCircle(clickPosX * stepSizeWidth, clickPosY * stepSizeHeight, 8, paint);
            canvas.drawText("(" + clickPosX + "/" + clickPosY + ")", 8, 13, paint);
            canvas.drawText("(" + (int)(clickPosX /x_factor) + "/" + (int)(clickPosY /y_factor) + ")", 8, 26, paint);
        }

        canvas.drawRect(
                0f,
                (float) (displayMetrics.heightPixels * buttonHeight),
                (float) displayMetrics.widthPixels,
                (float) displayMetrics.heightPixels,
                colorChangeOrientation);
    }

    public void setDisplayMetrics(final DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
        calculateDisplaySize();

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float y = motionEvent.getY();
                Log.d(TAG, "Touchend: " + y);
                if (y > displayMetrics.heightPixels * buttonHeight) {
                    ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    Log.d(TAG, "Orientation change requested");
                }
                return true;
            }
        });
    }

    private void calculateDisplaySize() {
        this.x_factor = displayMetrics.widthPixels / 180f;
        this.y_factor = displayMetrics.heightPixels / 180f;

        Log.d(TAG, "Display width in px is " + displayMetrics.widthPixels);
        Log.d(TAG, "Display height in px is " + displayMetrics.heightPixels);

    }

    /**
     *
     * @param x from 0 to 180
     * @param y from 0 to 180
     */
    public void setXYParams(float x, float y) {
        lineDrawEnabled = true;

        this.clickPosX = x;
        this.clickPosY = y;

        invalidate();
    }
}
