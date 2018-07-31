package io.viktorot.notefy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Collections;
import java.util.List;

import androidx.core.app.TaskStackBuilder;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.ui.main.MainActivity;

public class NotifyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            update(context, appWidgetManager, id);
        }
    }

    private void update(Context context, AppWidgetManager manager, int widgetId) {
        List<Note> notes = NotefyApplication
                .get(context).getNotesRepo().getLatestNoteList();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note_list);

        if (notes == null || notes.isEmpty()) {
            views.setViewVisibility(R.id.empty, View.VISIBLE);
            views.setViewVisibility(R.id.title, View.GONE);
            views.setViewVisibility(R.id.list, View.GONE);
        } else {
            views.setViewVisibility(R.id.empty, View.GONE);
            views.setViewVisibility(R.id.title, View.VISIBLE);
            views.setViewVisibility(R.id.list, View.VISIBLE);
            views.setRemoteAdapter(R.id.list, new Intent(context, NotifyWidgetRemoteViewService.class));

            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.list, pendingIntent);

            updateList(context);
        }

        manager.updateAppWidget(widgetId, views);
    }

    private void updateList(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, NotifyWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list);
    }

}
