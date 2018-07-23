package io.viktorot.notefy;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.FilterRelay;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.TagRepo;
import timber.log.Timber;

public class NotefyApplication extends Application {

    public static NotefyApplication get(@NonNull Context context) {
        return (NotefyApplication) context.getApplicationContext();
    }

    private Navigator navigator;

    private FilterRelay filterRelay = new FilterRelay();

    private AuthRepo authRepo;
    private NotesRepo notesRepo;
    private final IconRepo iconRepo = new IconRepo();
    private final ColorRepo colorRepo = new ColorRepo();
    private TagRepo tagRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        authRepo = new AuthRepo(FirebaseAuth.getInstance());
        notesRepo = new NotesRepo(FirebaseDatabase.getInstance());
        tagRepo = new TagRepo(getResources().getStringArray(R.array.tags));

        navigator = new Navigator(new NavEventRelay());
    }

    public IconRepo getIconRepo() {
        return this.iconRepo;
    }

    public ColorRepo getColorRepo() {
        return this.colorRepo;
    }

    public AuthRepo getAuthRepo() {
        return this.authRepo;
    }

    public NotesRepo getNotesRepo() {
        return this.notesRepo;
    }

    public TagRepo getTagRepo() {
        return tagRepo;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }

    public FilterRelay getFilterRelay() {
        return filterRelay;
    }
}
