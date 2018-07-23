package io.viktorot.notefy.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.ui.main.MainActivity;

public class NotificationUtils {

    private final Context context;
    private final IconRepo iconRepo;

    public NotificationUtils(Context context) {
        this.context = context;

        this.iconRepo = NotefyApplication.get(context).getIconRepo();
    }

    public void notify(@NonNull Note note) {
        if (note.isPinned()) {
            show(note);
        } else {
            remove(note);
        }
    }

    private void show(@NonNull Note note) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(String.valueOf(System.currentTimeMillis()));
        // TODO: open from notfication
        //intent.putExtra(NOTE_KEY, note.getKey());

        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: register channel id
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setOngoing(true);
        // TODO: set icon
        builder.setSmallIcon(iconRepo.getIconRes(note.getIconId()));
        builder.setContentTitle(note.getTitle());
        if (!TextUtils.isEmpty(note.getContent())) {
            builder.setContentText(note.getContent());
        }
        builder.setContentIntent(pendingIntent);


        int notificationId = Objects.hash(note.getKey());

        NotificationManagerCompat.from(context)
                .notify(notificationId, builder.build());
    }

    private void remove(@NonNull Note note) {
        int notificationId = Objects.hash(note.getKey());

        NotificationManagerCompat.from(context)
                .cancel(notificationId);
    }

}
