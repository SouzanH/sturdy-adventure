package ch.fhnw.android_labyrinth.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ch.fhnw.android_labyrinth.activity.MainActivity;

public class ClickView extends View {

    private static final String TAG = "ClickView";

    private Paint paint;

    private float x_factor = 1;
    private float y_factor = 1;

    private boolean lineDrawEnabled = false;
    private float clickPosX = 0;
    private float clickPosY = 0;

    private DisplayMetrics displayMetrics;
    private final Paint colorChangeOrientation;


    public ClickView(final Context context) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(40);


        colorChangeOrientation = new Paint();
        colorChangeOrientation.setColor(Color.RED);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                float x = motionEvent.getX();
                float y = motionEvent.getY();
                setXYParams(x, y);

                Log.d(TAG, "clicked (" + x + "/" + y +")");

                // Invalidate the paint area and redraw
                invalidate();

                return true;
            }
        });
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
            canvas.drawText("(" + clickPosX + "/" + clickPosY + ")", 8, 53, paint);
            canvas.drawText("(" + (int)(clickPosX /x_factor) + "/" + (int)(clickPosY /y_factor) + ")", 8, 106, paint);
        }


        canvas.drawRect(
                (float) (displayMetrics.widthPixels*.8),
                (float) (displayMetrics.heightPixels * .98),
                (float) (displayMetrics.widthPixels * .98),
                (float) (displayMetrics.heightPixels * .8), colorChangeOrientation);
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
}
