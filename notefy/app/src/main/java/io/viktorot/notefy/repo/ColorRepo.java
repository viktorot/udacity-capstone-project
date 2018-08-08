package io.viktorot.notefy.repo;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class ColorRepo {

    public static final List<String> COLORS = Arrays.asList(
            "#9e9e9e",
            "#f44336",
//            "#e91e63",
            "#9c27b0",
            "#673ab7",
            "#3f51b5",
            "#2196f3",
//            "#00bcd4",
//            "#009688",
//            "#4caf50",
            "#8bc34a",
//            "#cddc39",
            "#ffeb3b",
//            "#ffc107",
            "#ff9800"
//            "#ff5722"
    );

    public static final List<String> PAIR_COLOR = Arrays.asList(
            "707070",
            "ba000d",
            "b0003a"
    );

    @NonNull
    public static String getDefaultColor() {
        return COLORS.get(0);
    }

    @NonNull
    public static String getColor(int index) {
        if (index < 0 || index > COLORS.size() - 1) {
            return "";
        } else {
            return COLORS.get(index);
        }
    }

    public static int getColorIndex(@NonNull String color) {
        if (TextUtils.isEmpty(color)) {
            return -1;
        }

        for (int i = 0; i < COLORS.size(); i++) {
            if (getColor(i).equals(color)) {
                return i;
            }
        }

        return -1;
    }
}
