package io.viktorot.notefy.ui.details.tags;

import android.support.annotation.NonNull;
import android.view.View;

import com.xwray.groupie.Item;

import io.viktorot.notefy.R;
import io.viktorot.notefy.ui.view.Chip;

public class TagListViewItem extends Item<TagListViewItem.ViewHolder> {

    private final String name;

    public TagListViewItem(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder createViewHolder(@NonNull View itemView) {
        return new TagListViewItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.chip.setText(R.string.app_name);
    }

    @Override
    public int getLayout() {
        return R.layout.item_tag_list;
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        private final Chip chip;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            chip = rootView.findViewById(R.id.chip);
        }
    }
}
