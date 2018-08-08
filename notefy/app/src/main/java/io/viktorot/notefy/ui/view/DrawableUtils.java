package io.viktorot.notefy.ui.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

public class DrawableUtils {

    private DrawableUtils() {}

    /** Returns a tint filter for the given tint and mode. */
    @Nullable
    public static PorterDuffColorFilter updateTintFilter(
            Drawable drawable, @Nullable ColorStateList tint, @Nullable PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }

        final int color = tint.getColorForState(drawable.getState(), Color.TRANSPARENT);
        return new PorterDuffColorFilter(color, tintMode);
    }
}

