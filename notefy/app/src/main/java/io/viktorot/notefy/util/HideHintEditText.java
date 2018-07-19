package io.viktorot.notefy.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class HideHintEditText extends AppCompatEditText {

    private CharSequence cachedHint = "";

    public HideHintEditText(Context context) {
        super(context);
        init();
    }

    public HideHintEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HideHintEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cachedHint = getHint();

        setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                setHint("");
            } else {
                setHint(cachedHint);
            }
        });
    }
}
