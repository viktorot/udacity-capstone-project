package io.viktorot.notefy;

import android.app.Application;

import timber.log.Timber;

public class NotefyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
