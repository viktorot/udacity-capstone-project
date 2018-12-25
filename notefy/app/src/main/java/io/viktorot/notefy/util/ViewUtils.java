package io.viktorot.notefy.util;

import android.view.View;

import androidx.annotation.NonNull;

public class ViewUtils {

    private ViewUtils() {
    }

    public static void hide(@NonNull View view) {
        view.setVisibility(View.GONE);
    }

    public static void show(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void invisible(@NonNull View view) {
        view.setVisibility(View.INVISIBLE);
    }

}
