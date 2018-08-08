package io.viktorot.notefy.ui.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RestrictTo;

public class CanvasCompat {

    private CanvasCompat() {}

    /**
     * Convenience for {@link Canvas#saveLayer(RectF, Paint)} but instead of taking a entire Paint
     * object it takes only the {@code alpha} parameter.
     */
    public static int saveLayerAlpha(Canvas canvas, RectF bounds, int alpha) {
        if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayerAlpha(bounds, alpha);
        } else {
            return canvas.saveLayerAlpha(bounds, alpha, Canvas.ALL_SAVE_FLAG);
        }
    }

    /**
     * Convenience for {@link #saveLayerAlpha(Canvas, RectF, int)} that takes the four float
     * coordinates of the bounds rectangle.
     */
    public static int saveLayerAlpha(
            Canvas canvas, float left, float top, float right, float bottom, int alpha) {
        if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayerAlpha(left, top, right, bottom, alpha);
        } else {
            return canvas.saveLayerAlpha(left, top, right, bottom, alpha, Canvas.ALL_SAVE_FLAG);
        }
    }
}