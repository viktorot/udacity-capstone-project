package io.viktorot.notefy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import timber.log.Timber;

public class ClickableNestedScrollView extends NestedScrollView {

    public ClickableNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public ClickableNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                Timber.v("down");
                //return false;
                break;
            }
            case MotionEvent.ACTION_UP: {
                Timber.v("up");
                //return false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Timber.v("move");
                //return false;
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
