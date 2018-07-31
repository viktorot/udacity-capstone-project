package io.viktorot.notefy;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.FilterRelay;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.util.NotificationUtils;
import io.viktorot.notefy.widget.NotifyWidget;
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

    private NotificationUtils notificationUtils;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        authRepo = new AuthRepo(FirebaseAuth.getInstance());
        notesRepo = new NotesRepo(this, FirebaseDatabase.getInstance());
        tagRepo = new TagRepo(getResources().getStringArray(R.array.tags));

        navigator = new Navigator(new NavEventRelay());

        notificationUtils = new NotificationUtils(this);
    }

    public void updateWidgets() {
        Intent intent = new Intent(this, NotifyWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager
                .getInstance(this)
                .getAppWidgetIds(new ComponentName(this, NotifyWidget.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.sendBroadcast(intent);
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

    public NotificationUtils getNotificationUtils() {
        return notificationUtils;
    }
}
