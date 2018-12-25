package io.viktorot.notefy;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import io.viktorot.notefy.util.ConnectionRelay;
import timber.log.Timber;

public class ConnectionService extends Service {

    private static final int PING_INITIAL_DELAY = 5 * 1000;
    private static final int PING_INTERVAL = 60 * 1000;

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, ConnectionService.class);
    }

    private boolean scheduled = false;

    private final Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            new PingAsyncTask(
                    NotefyApplication.get(getApplication()).getConnectionRelay()
            ).execute();
        }
    };

    private void startPingTask() {
        if (scheduled) {
            return;
        }
        timer.schedule(task, PING_INITIAL_DELAY, PING_INTERVAL);
        scheduled = true;
    }

    private void stopPingTask() {
        timer.purge();
        timer.cancel();
        scheduled = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("starting...");
        startPingTask();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("stopping...");
        stopPingTask();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private class PingAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private static final String PING_URL = "http://www.google.com";

        private final ConnectionRelay relay;

        PingAsyncTask(ConnectionRelay relay) {
            this.relay = relay;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(PING_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Test");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.connect();

                return connection.getResponseCode() == 200;
            } catch (Exception e) {
                Timber.e(e, "ping failed");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            this.relay.post(connected);
        }
    }
}
