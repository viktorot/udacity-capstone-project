package io.viktorot.notefy.ui.details;

import android.app.Application;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class NoteDetailsViewModel extends AndroidViewModel {

    enum Action {
        SelectIcon,
        SelectColor,
        SelectTag,
        ShowEmptyTitleError,
        ShowDeleteConfirmation
    }

    private final Navigator navigator;

    private final IconRepo iconRepo;
    private final ColorRepo colorRepo;
    private final NotesRepo notesRepo;

    SingleLiveEvent<Action> action = new SingleLiveEvent<>();
    MutableLiveData<Note> data = new MutableLiveData<>();

    private boolean edited = false;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        iconRepo = NotefyApplication.get(application).getIconRepo();
        colorRepo = NotefyApplication.get(application).getColorRepo();
    }

    void init(@Nullable Note note) {
        if (note == null) {
            data.setValue(Note.empty());
        } else {
            data.setValue(note);
        }
    }

    private void dispatchAction(Action action) {
        this.action.setValue(action);
    }

    private void notifyDataChange() {
        Note note = data.getValue();
        data.setValue(note);
    }

    boolean isEdited() {
        return edited;
    }

    int getTagId() {
        Note note = data.getValue();
        if (note == null) {
            return TagRepo.ID_NONE;
        }
        return note.getTagId();
    }

    private void edited() {
        edited = true;
    }

    void save() {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (TextUtils.isEmpty(note.getTitle())) {
            Timber.w("empty title");
            dispatchAction(Action.ShowEmptyTitleError);
            return;
        }

        notesRepo.save(note);

        pop();
    }

    void delete() {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        notesRepo.delete(note);

        pop();
    }

    void selectIcon() {
        dispatchAction(Action.SelectIcon);
    }

    void selectColor() {
        dispatchAction(Action.SelectColor);
    }

    void selectTag() {
        dispatchAction(Action.SelectTag);
    }

    void deleteNote() {
        dispatchAction(Action.ShowDeleteConfirmation);
    }

    void pop() {
        navigator.pop();
    }

    void back() {
        navigator.back();
    }

    void togglePinnedState() {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        edited();

        note.setPinned(!note.isPinned());
        notifyDataChange();
    }

    void onTitleChanged(@NonNull String title) {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (title.equals(note.getTitle())) {
            return;
        }

        edited();

        note.setTitle(title);
    }

    void onContentChanged(@NonNull String content) {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (content.equals(note.getContent())) {
            return;
        }

        edited();

        note.setContent(content);
    }

    void onIconSelected(@DrawableRes int iconResId) {
        int iconId = iconRepo.getIconId(iconResId);
        Timber.d("icon id selected => %d", iconId);

        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (iconId == note.getIconId()) {
            return;
        }

        edited();

        note.setIconId(iconId);
        notifyDataChange();
    }

    void onColorSelected(String color) {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (color.equals(note.getColor())) {
            return;
        }

        edited = true;

        note.setColor(color);
        notifyDataChange();
    }

    void onTagSelected(int tagId) {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        if (tagId == note.getTagId()) {
            return;
        }

        edited = true;

        note.setTagId(tagId);
        notifyDataChange();
    }
}
