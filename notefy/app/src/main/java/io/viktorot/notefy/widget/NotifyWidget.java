package io.viktorot.notefy.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Collections;
import java.util.List;

import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;

public class NotifyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            update(context, appWidgetManager, id);
        }
    }

    private void update(Context context, AppWidgetManager manager, int widgetId) {
        List<Note> notes = Collections.emptyList();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note_list);

//        if (notes == null || notes.isEmpty()) {
//            views.setViewVisibility(R.id.empty, View.VISIBLE);
//            views.setTextViewText(R.id.empty, context.getString(R.string.no_notes_available));
//            views.setViewVisibility(R.id.list, View.GONE);
//        } else {
//            views.setViewVisibility(R.id.empty, View.GONE);
//            views.setViewVisibility(R.id.list, View.VISIBLE);
//            views.setRemoteAdapter(R.id.list, new Intent(context, NotifyWidgetRemoteViewService.class));
//        }

        views.setViewVisibility(R.id.empty, View.GONE);
        views.setViewVisibility(R.id.list, View.VISIBLE);
        views.setViewVisibility(R.id.title, View.VISIBLE);
        views.setRemoteAdapter(R.id.list, new Intent(context, NotifyWidgetRemoteViewService.class));

        manager.updateAppWidget(widgetId, views);
    }

}
