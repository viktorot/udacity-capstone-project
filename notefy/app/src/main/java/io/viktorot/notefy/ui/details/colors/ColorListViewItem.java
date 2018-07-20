package io.viktorot.notefy.ui.details.colors;

import android.graphics.Color;
import android.view.View;

import com.xwray.groupie.Item;

import androidx.annotation.NonNull;
import io.viktorot.notefy.R;

public class ColorListViewItem extends Item<ColorListViewItem.ViewHolder> {

    private final String color;

    public ColorListViewItem(String color) {
        this.color = color;
    }

    @NonNull
    @Override
    public ViewHolder createViewHolder(@NonNull View itemView) {
        return new ColorListViewItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.viewColor.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getLayout() {
        return R.layout.item_color_list;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount / 2;
    }

    @NonNull
    public String getColor() {
        return color;
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        private final View viewColor;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            viewColor = rootView.findViewById(R.id.color);
        }
    }
}
