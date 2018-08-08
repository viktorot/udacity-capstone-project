package io.viktorot.notefy.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    private KeyboardUtils() {
    }

    public static void hideKeyboard(FragmentActivity activity) {
        View v = activity.getCurrentFocus();
        if (v != null) {
            v.clearFocus();
            hideKeyboard(activity, v);
        }
    }

    public static void hideKeyboard(FragmentActivity activity, View view) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(FragmentActivity activity, View view) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

}
