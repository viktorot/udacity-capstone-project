package io.viktorot.notefy.repo;

import android.support.annotation.DrawableRes;

import java.util.HashMap;

import io.viktorot.notefy.R;

public class IconRepo {

    public static final int ID_ANDROID = 0;
    public static final int ID_ATTACH = 1;
    public static final int ID_BOOKMARK = 2;
    public static final int ID_CHECK = 3;
    public static final int ID_DOC = 4;
    public static final int ID_DOT = 5;
    public static final int ID_EXTENSION = 6;
    public static final int ID_FACE = 7;
    public static final int ID_FOLDER = 8;
    public static final int ID_RUN = 9;

    private static final HashMap<Integer, Integer> resToIdMap = new HashMap<>();
    private static final HashMap<Integer, Integer> idToResMap = new HashMap<>();

    public IconRepo() {
        resToIdMap.put(R.drawable.ic_android, ID_ANDROID);
        resToIdMap.put(R.drawable.ic_attach, ID_ATTACH);
        resToIdMap.put(R.drawable.ic_bookmark, ID_BOOKMARK);
        resToIdMap.put(R.drawable.ic_check, ID_CHECK);
        resToIdMap.put(R.drawable.ic_doc, ID_DOC);
        resToIdMap.put(R.drawable.ic_dot, ID_DOT);
        resToIdMap.put(R.drawable.ic_extension, ID_EXTENSION);
        resToIdMap.put(R.drawable.ic_face, ID_FACE);
        resToIdMap.put(R.drawable.ic_folder, ID_FOLDER);
        resToIdMap.put(R.drawable.ic_run, ID_RUN);

        idToResMap.put(ID_ANDROID, R.drawable.ic_android);
        idToResMap.put(ID_ATTACH, R.drawable.ic_attach);
        idToResMap.put(ID_BOOKMARK, R.drawable.ic_bookmark);
        idToResMap.put(ID_CHECK, R.drawable.ic_check);
        idToResMap.put(ID_DOC, R.drawable.ic_doc);
        idToResMap.put(ID_DOT, R.drawable.ic_dot);
        idToResMap.put(ID_EXTENSION, R.drawable.ic_extension);
        idToResMap.put(ID_FACE, R.drawable.ic_face);
        idToResMap.put(ID_FOLDER, R.drawable.ic_folder);
        idToResMap.put(ID_RUN, R.drawable.ic_run);
    }

    public static int getDefaultIconId() {
        return ID_ANDROID;
    }

    public int getIconId(@DrawableRes int iconResId) {
        return resToIdMap.get(iconResId);
    }

    public int getIconRes(int iconId) {
        return idToResMap.get(iconId);
    }

}
