package ch.fhnw.android_labyrinth.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import ch.fhnw.android_labyrinth.R;

public class ConnectActivity extends AppCompatActivity {

    public static final String EXTRA_CONNECTION = "EXTRA_CONNECTION";

    private static final String SERVER_PREFS = "ServerPrefs";

    private static final String DEFAULT_IP = "10.0.2.2";
    private static final String DEFAULT_PORT = "12002";

    private ProgressBar pbConnect;
    private EditText etPort;
    private EditText etIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        pbConnect = (ProgressBar) findViewById(R.id.progressConnect);
        etIp = (EditText) findViewById(R.id.editIP);
        etPort = (EditText) findViewById(R.id.editPort);

        SharedPreferences settings = getSharedPreferences(SERVER_PREFS, MODE_PRIVATE);
        etIp.setText(settings.getString("ip", ""));
        etPort.setText(settings.getString("port", ""));
    }

    public void onButtonClick (View v) {

        SharedPreferences settings = getSharedPreferences(SERVER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ip", "" + etIp.getText());
        editor.putString("port", "" + etPort.getText());
        editor.apply();

        connectToMachine();
    }

    private void connectToMachine() {
        pbConnect.setVisibility(View.VISIBLE);
        new ServerConnector().execute();

    }

    private class ServerConnector extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
                // 20 % failure rate
                return Math.random() < 0.8;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            super.onPostExecute(successful);
            pbConnect.setVisibility(View.GONE);
            if (!successful) {
                Toast toast = Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

}
