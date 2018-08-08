package io.viktorot.notefy.ui.view;

public class TextAppearanceConfig {

    private static boolean shouldLoadFontSynchronously;

    /**
     * Specifies whether font resources should be loaded synchronously. By default, they are loaded
     * asynchronously to avoid ANR. Preload font resources and set this to true in emulator /
     * instrumentation tests to avoid flakiness.
     */
    public static void setShouldLoadFontSynchronously(boolean flag) {
        shouldLoadFontSynchronously = flag;
    }

    /** Returns flag indicating whether font resources should be loaded synchronously. */
    public static boolean shouldLoadFontSynchronously() {
        return shouldLoadFontSynchronously;
    }
}
