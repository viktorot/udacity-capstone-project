package io.viktorot.notefy.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;

public class Note implements Parcelable {

    @Exclude
    private String key;
    private String title;
    private String content;
    private int iconId;
    private String color;
    private boolean pinned;

    @NonNull
    public static Note empty() {
        Note note = new Note();
        note.setKey(null);
        note.setTitle("");
        note.setContent("");
        note.setIconId(IconRepo.getDefaultIconId());
        note.setColor(ColorRepo.getDefaultColor());
        note.setPinned(false);

        return note;
    }

    public Note() {
    }

    @Nullable
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(@NonNull String color) {
        this.color = color;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.iconId);
        dest.writeString(this.color);
        dest.writeByte(this.pinned ? (byte) 1 : (byte) 0);
    }

    protected Note(Parcel in) {
        this.key = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.iconId = in.readInt();
        this.color = in.readString();
        this.pinned = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
