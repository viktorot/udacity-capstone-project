package io.viktorot.notefy.ui.details;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.IconRepo;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.util.NotificationUtils;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class NoteDetailsViewModel extends AndroidViewModel {

    enum Action {
        SelectIcon,
        SelectColor,
        SelectTag,
        ShowEmptyTitleError,
        ShowDeleteConfirmation,

        ShowProgress,
        HideProgress
    }

    enum Change {
        Content, Pin
    }

    private final Navigator navigator;

    private final IconRepo iconRepo;
    private final ColorRepo colorRepo;
    private final NotesRepo notesRepo;

    private final NotificationUtils notificationUtils;

    SingleLiveEvent<Action> action = new SingleLiveEvent<>();
    MutableLiveData<Note> data = new MutableLiveData<>();

    private Change change = null;
    private boolean edited = false;

    private Disposable changesDisposable = null;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        iconRepo = NotefyApplication.get(application).getIconRepo();
        colorRepo = NotefyApplication.get(application).getColorRepo();

        notificationUtils = NotefyApplication.get(application).getNotificationUtils();
    }

    void init(@Nullable Note note) {
        if (note == null) {
            data.setValue(Note.empty());
        } else {
            data.setValue(note);
        }

        changesDisposable = notesRepo.noteChanges
                .filter(event -> {
                    Note n = NoteDetailsViewModel.this.data.getValue();

                    return n != null &&
                            ((!TextUtils.isEmpty(n.getKey()) &&
                            n.getKey().equals(event.data().getKey())) ||
                            TextUtils.isEmpty(n.getKey()));
                })
                .subscribe(event -> {

                    dispatchAction(Action.HideProgress);

                    if (event instanceof NotesRepo.Event.Added) {
                        //NoteDetailsViewModel.this.data.setValue(event.data());
                        notificationUtils.notify(event.data());
                        pop();
                    } else if (event instanceof NotesRepo.Event.Changed) {
                        NoteDetailsViewModel.this.data.setValue(event.data());
                        notificationUtils.notify(event.data());
                        if (change == Change.Pin) {
                            edited(false);
                        } else {
                            pop();
                        }
                    } else if (event instanceof NotesRepo.Event.Removed) {
                        notificationUtils.remove(event.data());
                        pop();
                    }

                    change = null;
                });
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

    private void edited(boolean value) {
        edited = value;
    }

    void edited() {
        edited(true);
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

        dispatchAction(Action.ShowProgress);

        change = Change.Content;

        notesRepo.save(note);

//        notesRepo.save(note, new NotesRepo.SaveTaskCallback() {
//            @Override
//            public void onSuccess(String key) {
//                // TODO: get new key
//                note.setKey(key);
//                notificationUtils.notify(note);
//                pop();
//
//                dispatchAction(Action.HideProgress);
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                Timber.e(exception, "failed to save note");
//                // TODO: show toast
//
//                dispatchAction(Action.HideProgress);
//            }
//        });
    }

    void delete() {
        Note note = data.getValue();
        if (note == null) {
            Timber.w("note not set");
            return;
        }

        notesRepo.delete(note);
//        notesRepo.delete(note, new NotesRepo.TaskCallback() {
//            @Override
//            public void onSuccess() {
//                notificationUtils.remove(note);
//                pop();
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                Timber.e(exception, "failed to delete note");
//                // TODO: show toast
//            }
//        });
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

        if (note.isNew()) {
            note.setPinned(!note.isPinned());
            notifyDataChange();
        } else {
            boolean newPinnedState = !note.isPinned();
            note.setPinned(newPinnedState);

            notifyDataChange();

            dispatchAction(Action.ShowProgress);

            change = Change.Pin;

            notesRepo.pin(note);

//            notesRepo.pin(note, new NotesRepo.TaskCallback() {
//                @Override
//                public void onSuccess() {
//                    Timber.d("pin state updated");
//                    notificationUtils.notify(note);
//                    edited(false);
//
//                    dispatchAction(Action.HideProgress);
//                }
//
//                @Override
//                public void onError(Exception exception) {
//                    Timber.e(exception, "updating pin state failed");
//
//                     TODO: show toast
//                    note.setPinned(!newPinnedState);
//                    notifyDataChange();
//
//                    dispatchAction(Action.HideProgress);
//                }
//            });
        }
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

        edited();

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

        edited();

        note.setTagId(tagId);
        notifyDataChange();
    }

    @Override
    protected void onCleared() {
        if (changesDisposable != null) {
            changesDisposable.dispose();
        }
        super.onCleared();
    }
}
