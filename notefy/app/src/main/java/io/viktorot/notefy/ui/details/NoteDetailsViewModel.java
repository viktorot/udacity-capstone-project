package io.viktorot.notefy.ui.details;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class NoteDetailsViewModel extends AndroidViewModel {

    enum Action {
        SelectIcon,
        SelectColor,
        ShowEmptyTitleError
    }

    private final Navigator navigator;

    private final IconRepo iconRepo;
    private final ColorRepo colorRepo;
    private final NotesRepo notesRepo;

    SingleLiveEvent<Action> action = new SingleLiveEvent<>();
    MutableLiveData<Note> data = new MutableLiveData<>();

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        iconRepo = NotefyApplication.get(application).getIconRepo();
        colorRepo = NotefyApplication.get(application).getColorRepo();

        data.setValue(Note.empty());
    }

    private void dispatchAction(Action action) {
        this.action.setValue(action);
    }

    private void notifyDataChange() {
        Note note = data.getValue();
        data.setValue(note);
    }

    void saveNote(@NonNull String title, @NonNull String content) {
        if (TextUtils.isEmpty(title)) {
            Timber.w("empty title");
            dispatchAction(Action.ShowEmptyTitleError);
            return;
        }

        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        note.setTitle(title);
        note.setContent(content);

        notifyDataChange();

        notesRepo.save(note);

        close();
    }

    void selectIcon() {
        dispatchAction(Action.SelectIcon);
    }

    void selectColor() {
        dispatchAction(Action.SelectColor);
    }

    void close() {
        navigator.back();
    }

    void togglePinnedState() {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        note.setPinned(!note.isPinned());
        notifyDataChange();
    }

    void onIconSelected(@DrawableRes int iconResId) {
        int iconId = iconRepo.getIconId(iconResId);
        Timber.d("icon id selected => %d", iconId);

        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        note.setIconId(iconId);
        notifyDataChange();
    }

    void onColorSelected(String color) {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        note.setColor(color);
        notifyDataChange();
    }
}
