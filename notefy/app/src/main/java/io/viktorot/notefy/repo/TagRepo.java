package io.viktorot.notefy.repo;

import androidx.annotation.NonNull;

public class TagRepo {

    private final String[] tags;

    public TagRepo(@NonNull String[] tags) {
        this.tags = tags;
    }

    public static int getDefaultTagId() {
        return -1;
    }

    @NonNull
    public String[] getTags() {
        return tags;
    }

    @NonNull
    public String getTag(int id) {
        if (id > tags.length - 1) {
            return tags[0];
        } else {
            return tags[id];
        }
    }
}
