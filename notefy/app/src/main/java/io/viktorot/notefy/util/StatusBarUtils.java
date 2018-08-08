package io.viktorot.notefy.util;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class StatusBarUtils {

    public static void setColor(@NonNull FragmentActivity activity, @NonNull int color) {
        Window window = activity.getWindow();
        window.setStatusBarColor(color);
    }

}
