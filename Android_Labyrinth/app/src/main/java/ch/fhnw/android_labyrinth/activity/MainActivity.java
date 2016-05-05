package ch.fhnw.android_labyrinth.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import ch.fhnw.android_labyrinth.view.ClickView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String ip;
    private String port;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final ClickView view = new ClickView(this);
        view.setDisplayMetrics(displayMetrics);
        setContentView(view);


        final Bundle extras = getIntent().getExtras();
        // TODO get connection object
//        if (extras != null) {
//            ip = extras.getString(ConnectActivity.EXTRA_IP_ADDRESS);
//            port = extras.getString(ConnectActivity.EXTRA_IP_PORT);
//
//        }
    }


}
