package io.viktorot.notefy;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxrelay2.PublishRelay;

import androidx.annotation.NonNull;
import io.viktorot.notefy.navigator.NavEvent;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.IconRepo;
import timber.log.Timber;

public class NotefyApplication extends Application {

    public static NotefyApplication get(@NonNull Context context) {
        return (NotefyApplication) context.getApplicationContext();
    }

    private PublishRelay<NavEvent> navEventRelay = PublishRelay.create();

    private Navigator navigator;

    private AuthRepo authRepo;
    private NotesRepo notesRepo;

    private final IconRepo iconsRepo = new IconRepo();

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        authRepo = new AuthRepo(FirebaseAuth.getInstance());
        notesRepo = new NotesRepo(FirebaseDatabase.getInstance());

        navigator = new Navigator();
    }

    public IconRepo getIconsRepo() {
        return this.iconsRepo;
    }

    public AuthRepo getAuthRepo() {
        return this.authRepo;
    }

    public NotesRepo getNotesRepo() {
        return this.notesRepo;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }
}
