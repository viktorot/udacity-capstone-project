package io.viktorot.notefy.repo;

import android.support.annotation.NonNull;

public class TagRepo {

    public static final int ID_NONE = 0;

    private final String[] tags;

    public TagRepo(@NonNull String[] tags) {
        this.tags = tags;
    }

    public static int getDefaultTagId() {
        return ID_NONE;
    }

    @NonNull
    public String[] getTags() {
        return tags;
    }

    @NonNull
    public String getTag(int id) {
        if (!isIdValid(id)) {
            throw new IllegalArgumentException("invalid id => " + id);
        }

        return tags[id - 1];
    }

    // ids are setup to be 1 greater than the array index
    public boolean isIdValid(int id) {
        return id > 0 && (id - 1) < tags.length;
    }
}
