package io.viktorot.notefy.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.v7.content.res.AppCompatResources;

public class MaterialResources {

    private MaterialResources() {}

    /**
     * Returns the {@link ColorStateList} from the given attributes. The resource can include
     * themeable attributes, regardless of API level.
     */
    @Nullable
    public static ColorStateList getColorStateList(
            Context context, TypedArray attributes, @StyleableRes int index) {
        if (attributes.hasValue(index)) {
            int resourceId = attributes.getResourceId(index, 0);
            if (resourceId != 0) {
                ColorStateList value = AppCompatResources.getColorStateList(context, resourceId);
                if (value != null) {
                    return value;
                }
            }
        }
        return attributes.getColorStateList(index);
    }

    /**
     * Returns the drawable object from the given attributes.
     *
     * <p>This method supports inflation of {@code <vector>} and {@code <animated-vector>} resources
     * on devices where platform support is not available.
     */
    @Nullable
    public static Drawable getDrawable(
            Context context, TypedArray attributes, @StyleableRes int index) {
        if (attributes.hasValue(index)) {
            int resourceId = attributes.getResourceId(index, 0);
            if (resourceId != 0) {
                Drawable value = AppCompatResources.getDrawable(context, resourceId);
                if (value != null) {
                    return value;
                }
            }
        }
        return attributes.getDrawable(index);
    }

    /**
     * Returns a TextAppearanceSpan object from the given attributes.
     *
     * <p>You only need this if you are drawing text manually. Normally, TextView takes care of this.
     */
    @Nullable
    public static TextAppearance getTextAppearance(
            Context context, TypedArray attributes, @StyleableRes int index) {
        if (attributes.hasValue(index)) {
            int resourceId = attributes.getResourceId(index, 0);
            if (resourceId != 0) {
                return new TextAppearance(context, resourceId);
            }
        }
        return null;
    }

    /**
     * Returns the @StyleableRes index that contains value in the attributes array. If both indices
     * contain values, the first given index takes precedence and is returned.
     */
    @StyleableRes
    static int getIndexWithValue(TypedArray attributes, @StyleableRes int a, @StyleableRes int b) {
        if (attributes.hasValue(a)) {
            return a;
        }
        return b;
    }
}