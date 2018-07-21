package io.viktorot.notefy.util;

import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class StatusBarUtils {

    public static void setColor(@NonNull FragmentActivity activity, @NonNull int color) {
        Window window = activity.getWindow();
        window.setStatusBarColor(color);
    }

}
