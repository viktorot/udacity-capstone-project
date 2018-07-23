package io.viktorot.notefy.ui.list;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.util.ViewUtils;

class NoteListViewItem extends Item<NoteListViewItem.ViewHolder> {

    private final Note data;

    private final TagRepo tagRepo;

    NoteListViewItem(@NonNull Context context, @NonNull Note data) {
        this.data = data;
        this.tagRepo = NotefyApplication.get(context).getTagRepo();
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
        viewHolder.tvContent.setText(this.data.getContent());

        int id = this.data.getTagId();
        if (tagRepo.isIdValid(id)) {
            viewHolder.tvTag.setText(tagRepo.getTag(id));
            ViewUtils.show(viewHolder.tvTag);
        } else {
            ViewUtils.hide(viewHolder.tvTag);
        }

        viewHolder.imgCorner.setColorFilter(Color.parseColor(this.data.getColor()));
    }

    @Override
    public int getLayout() {
        return R.layout.item_note_list;
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        final TextView tvTitle;
        final TextView tvContent;
        final TextView tvTag;
        final ImageView imgCorner;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            tvTitle = rootView.findViewById(R.id.title);
            tvContent = rootView.findViewById(R.id.content);
            tvTag = rootView.findViewById(R.id.tag);
            imgCorner = rootView.findViewById(R.id.corner);
        }
    }
}
