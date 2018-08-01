package io.viktorot.notefy.ui.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import io.github.mthli.knife.KnifeText;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.util.KnifeTextView;
import io.viktorot.notefy.util.ViewUtils;

class NoteListViewItem extends Item<NoteListViewItem.ViewHolder> {

    private final Context context;

    private final Note data;

    private final TagRepo tagRepo;
    private final IconRepo iconRepo;

    NoteListViewItem(@NonNull Context context, @NonNull Note data) {
        this.context = context;
        this.data = data;

        this.tagRepo = NotefyApplication.get(context).getTagRepo();
        this.iconRepo = NotefyApplication.get(context).getIconRepo();
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

        viewHolder.tvContent.fromHtml(this.data.getContent());

        int id = this.data.getTagId();
        if (tagRepo.isIdValid(id)) {
            viewHolder.tvTag.setText(tagRepo.getTag(id));
            ViewUtils.show(viewHolder.tvTag);
            ViewUtils.show(viewHolder.imgTag);
        } else {
            ViewUtils.invisible(viewHolder.tvTag);
            ViewUtils.invisible(viewHolder.imgTag);
        }

        Drawable icon = ContextCompat.getDrawable(context, iconRepo.getIconRes(data.getIconId()));
        viewHolder.imgIcon.setImageDrawable(icon);

        if (this.data.isPinned()) {
            ViewUtils.show(viewHolder.imgPin);
        } else {
            ViewUtils.hide(viewHolder.imgPin);
        }

        viewHolder.imgCorner.setColorFilter(Color.parseColor(this.data.getColor()));
    }

    @Override
    public int getLayout() {
        return R.layout.item_note_list;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteListViewItem iv = (NoteListViewItem) o;
        return iv.data.equals(this.data);
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        final TextView tvTitle;
        final KnifeTextView tvContent;
        final TextView tvTag;
        final ImageView imgTag;
        final ImageView imgCorner;
        final ImageView imgPin;
        final ImageView imgIcon;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            tvTitle = rootView.findViewById(R.id.title);
            tvContent = rootView.findViewById(R.id.content);
            tvTag = rootView.findViewById(R.id.tag_text);
            imgTag = rootView.findViewById(R.id.tag_img);
            imgCorner = rootView.findViewById(R.id.corner);
            imgPin = rootView.findViewById(R.id.pin);
            imgIcon = rootView.findViewById(R.id.icon);

            imgIcon.setColorFilter(Color.WHITE);
            imgTag.setColorFilter(ContextCompat.getColor(context, R.color.light_gray));
        }
    }
}
