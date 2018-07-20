package io.viktorot.notefy.repo;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class ColorRepo {

    public static final List<String> COLORS = Arrays.asList(
            "#9e9e9e",
            "#f44336",
            "#e91e63",
            "#9c27b0",
            "#673ab7",
            "#3f51b5",
            "#2196f3",
            "#00bcd4",
            "#009688",
            "#4caf50",
            "#8bc34a",
            "#cddc39",
            "#ffeb3b",
            "#ffc107",
            "#ff9800",
            "#ff5722"
    );

    @NonNull
    public static String getDefaultColor() {
        return COLORS.get(0);
    }
}
