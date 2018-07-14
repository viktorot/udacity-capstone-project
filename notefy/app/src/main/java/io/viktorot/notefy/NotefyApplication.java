package io.viktorot.notefy;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import io.viktorot.notefy.repo.AuthRepo;
import timber.log.Timber;

public class NotefyApplication extends Application {

    public static NotefyApplication get(@NonNull Context context) {
        return (NotefyApplication) context.getApplicationContext();
    }

    private Navigator navigator;

    private AuthRepo authRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        authRepo = new AuthRepo(FirebaseAuth.getInstance());

        navigator = new Navigator();
    }

    public AuthRepo getAuthRepo() {
        return this.authRepo;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }
}
