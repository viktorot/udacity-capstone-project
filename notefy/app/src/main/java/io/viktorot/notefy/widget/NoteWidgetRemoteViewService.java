package io.viktorot.notefy.widget;

import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;

public class NoteWidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private List<Note> notes;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                // TODO: get note list
                //recipe = Prefs.getFavRecipe(getApplicationContext());
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
                views.setTextViewText(R.id.title, note.getTitle());
                views.setTextViewText(R.id.content, note.getContent());

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
