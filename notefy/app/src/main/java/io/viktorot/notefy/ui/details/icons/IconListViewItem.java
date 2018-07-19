package io.viktorot.notefy.ui.details.icons;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.xwray.groupie.Item;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import io.viktorot.notefy.R;

public class IconListViewItem extends Item<IconListViewItem.ViewHolder> {

    private final int iconResId;
    private final Drawable iconDrawable;

    IconListViewItem(@DrawableRes int iconResId, @NonNull Drawable iconDrawable) {
        this.iconResId = iconResId;
        this.iconDrawable = iconDrawable;
    }

    @NonNull
    @Override
    public ViewHolder createViewHolder(@NonNull View itemView) {
        return new IconListViewItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.imgIcon.setImageDrawable(iconDrawable);
    }

    @Override
    public int getLayout() {
        return R.layout.item_icon_list;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount / 2;
    }

    @DrawableRes
    int getIconResId() {
        return this.iconResId;
    }

    class ViewHolder extends com.xwray.groupie.ViewHolder {
        private final ImageView imgIcon;

        ViewHolder(@NonNull View rootView) {
            super(rootView);
            imgIcon = rootView.findViewById(R.id.icon);
        }
    }
}
