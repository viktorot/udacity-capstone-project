package io.viktorot.notefy.ui.view;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TouchDelegateGroup extends TouchDelegate {
    private static final Rect IGNORED = new Rect();

    private List<TouchDelegate> touchDelegates;
    @Nullable private TouchDelegate currentTouchDelegate;

    public TouchDelegateGroup(View ancestor) {
        super(IGNORED, ancestor);

        touchDelegates = new ArrayList<>();
    }

    public void addTouchDelegate(TouchDelegate touchDelegate) {
        if (touchDelegate == null) {
            throw new NullPointerException("Cannot add null touchDelegate");
        }
        touchDelegates.add(touchDelegate);
    }

    public void removeTouchDelegate(TouchDelegate touchDelegate) {
        touchDelegates.remove(touchDelegate);
        if (touchDelegate == currentTouchDelegate) {
            currentTouchDelegate = null;
        }
    }

    public void clearTouchDelegates() {
        touchDelegates.clear();
    }

    @VisibleForTesting
    public List<TouchDelegate> getTouchDelegates() {
        return Collections.unmodifiableList(new ArrayList<>(touchDelegates));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // Ignore all pointers except the first.
            if (event.getPointerCount() > 1) {
                return false;
            }
            for (int i = touchDelegates.size() - 1; i >= 0; i--) {
                TouchDelegate touchDelegate = touchDelegates.get(i);

                // Save event coordinates since TouchDelegate mutates the location.
                float savedX = event.getX();
                float savedY = event.getY();
                boolean handled = touchDelegate.onTouchEvent(event);
                // Restore event coordinates that we can bubble up to parent.
                event.setLocation(savedX, savedY);

                if (handled) {
                    currentTouchDelegate = touchDelegate;
                    return true;
                }
            }
            return false;
        }

        boolean handled = currentTouchDelegate != null && currentTouchDelegate.onTouchEvent(event);

        if (action == KeyEvent.ACTION_UP || action == KeyEvent.FLAG_CANCELED) {
            currentTouchDelegate = null;
        }

        return handled;
    }
}

