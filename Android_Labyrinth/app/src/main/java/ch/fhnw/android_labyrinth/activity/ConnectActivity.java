package ch.fhnw.android_labyrinth.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import ch.fhnw.android_labyrinth.LabyrinthRegistry;
import ch.fhnw.android_labyrinth.R;
import oscP5.OscP5;
import oscP5.OscProperties;

public class ConnectActivity extends Activity {

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
        String host = "".equals(etIp.getText().toString()) ? DEFAULT_IP : etIp.getText().toString();
        String port = "".equals(etPort.getText().toString()) ? DEFAULT_PORT : etPort.getText().toString();
        new ServerConnector().execute(host, port);

    }

    private class ServerConnector extends AsyncTask<String, Void, OscP5> {

        @Override
        protected OscP5 doInBackground(String... input) {

            OscProperties oscProperties = new OscProperties() ;
            oscProperties.setNetworkProtocol(OscProperties.TCP);
            oscProperties.setRemoteAddress(input[0], Integer.parseInt(input[1]));

            return new OscP5(this, oscProperties);
        }

        @Override
        protected void onPostExecute(OscP5 oscP5) {
            super.onPostExecute(oscP5);
            pbConnect.setVisibility(View.GONE);
            if (oscP5 == null) {
                // TODO proper fail handling..., oscp5 won't be null in case of error
                Toast toast = Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT);
                toast.show();

            } else {

                Toast toast = Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_SHORT);
                toast.show();



                LabyrinthRegistry.oscP5 = oscP5;
                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

}
