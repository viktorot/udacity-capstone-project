package io.viktorot.notefy.data;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Note {

    private String key;
    private String title;
    private String content;

    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.key = null;
    }

    @Nullable
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(key, note.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}