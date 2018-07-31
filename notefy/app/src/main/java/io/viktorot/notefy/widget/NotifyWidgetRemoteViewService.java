package io.viktorot.notefy.widget;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.ui.main.MainActivity;
import io.viktorot.notefy.util.NotificationUtils;

public class NotifyWidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private List<Note> notes;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                notes = NotefyApplication
                        .get(getApplicationContext()).getNotesRepo().getLatestNoteList();
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                if (notes == null || notes.size() == 0) {
                    return 0;
                }
                return notes.size();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_note);
                Note note = notes.get(i);

                views.setInt(R.id.root, "setBackgroundColor", Color.parseColor(note.getColor()));
                views.setTextViewText(R.id.title, note.getTitle());
                views.setTextViewText(R.id.content, Html.fromHtml(note.getContent()));

                Intent fillIntent = new Intent();
                fillIntent.putExtra(NotificationUtils.NOTE_DATA, note);
                views.setOnClickFillInIntent(R.id.root, fillIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
