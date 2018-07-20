package io.viktorot.notefy.ui.details;

import android.app.Application;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class NoteDetailsViewModel extends AndroidViewModel {

    enum Action {
        SelectIcon, SelectColor
    }

    private final Navigator navigator;

    private final IconRepo iconRepo;
    private final ColorRepo colorRepo;

    SingleLiveEvent<Action> action = new SingleLiveEvent<>();
    MutableLiveData<Note> data = new MutableLiveData<>();

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        iconRepo = NotefyApplication.get(application).getIconRepo();
        colorRepo = NotefyApplication.get(application).getColorRepo();

        Note note = new Note();
        note.setIconId(iconRepo.getDefaultIconId());
        note.setColor(colorRepo.getDefaultColor());

        data.setValue(note);
    }

    private void dispatchAction(Action action) {
        this.action.setValue(action);
    }

    private void notifyDataChange() {
        Note note = data.getValue();
        data.setValue(note);
    }

    void selectIcon() {
        dispatchAction(Action.SelectIcon);
    }

    void selectColor() {
        dispatchAction(Action.SelectColor);
    }

    void back() {
        navigator.back();
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
