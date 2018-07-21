package io.viktorot.notefy.ui.list;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;

class NoteListViewItem extends Item<NoteListViewItem.ViewHolder> {

    private final Note data;

    NoteListViewItem(@NonNull Note data) {
        this.data = data;
    }

    @NonNull
    public Note getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder createViewHolder(@NonNull View itemView) {
        return new NoteListViewItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull NoteListViewItem.ViewHolder viewHolder, int position) {
        viewHolder.tvTitle.setText(this.data.getTitle());
        viewHolder.imgCorner.setColorFilter(Color.parseColor(this.data.getColor()));
    }

    @Override
    public int getLayout() {
        return R.layout.item_note_list;
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        final TextView tvTitle;
        final ImageView imgCorner;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            tvTitle = rootView.findViewById(R.id.title);
            imgCorner = rootView.findViewById(R.id.corner);
        }
    }
}
