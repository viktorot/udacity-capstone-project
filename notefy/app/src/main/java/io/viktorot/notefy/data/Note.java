package io.viktorot.notefy.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.TagRepo;

public class Note implements Parcelable {

    @Exclude
    private String key;
    private String title;
    private String content;
    private int tagId;
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
        note.setTagId(TagRepo.getDefaultTagId());
        note.setPinned(false);

        return note;
    }

    @NonNull
    public static Note copy(@NonNull Note note) {
        Objects.requireNonNull(note);

        Note copy = new Note();
        copy.setKey(note.getKey());
        copy.setTitle(note.getTitle());
        copy.setContent(note.getContent());
        copy.setIconId(note.getIconId());
        copy.setColor(note.getColor());
        copy.setPinned(note.isPinned());
        copy.setTagId(note.getTagId());

        return copy;
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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @Exclude
    public boolean isNew() {
        return TextUtils.isEmpty(getKey());
    }

    @Exclude
    public Map<String, Object> getUpdateMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("color", getColor());
        result.put("title", getTitle());
        result.put("content", getContent());
        result.put("iconId", getIconId());
        result.put("pinned", isPinned());
        result.put("tagId", getTagId());

        return result;
    }

    @Exclude
    public Map<String, Object> getPinUpdateMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pinned", isPinned());

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return tagId == note.tagId &&
                iconId == note.iconId &&
                pinned == note.pinned &&
                Objects.equals(key, note.key) &&
                Objects.equals(title, note.title) &&
                Objects.equals(content, note.content) &&
                Objects.equals(color, note.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, title, content, tagId, iconId, color, pinned);
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
        dest.writeInt(this.tagId);
        dest.writeInt(this.iconId);
        dest.writeString(this.color);
        dest.writeByte(this.pinned ? (byte) 1 : (byte) 0);
    }

    protected Note(Parcel in) {
        this.key = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.tagId = in.readInt();
        this.iconId = in.readInt();
        this.color = in.readString();
        this.pinned = in.readByte() != 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
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
