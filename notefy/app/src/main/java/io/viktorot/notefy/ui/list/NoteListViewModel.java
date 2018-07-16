package io.viktorot.notefy.ui.list;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.NotesRepo;
import timber.log.Timber;

class NoteListViewModel extends AndroidViewModel {

    enum State {
        Unauthorized, Loading, Empty, Data
    }

    private final AuthRepo authRepo;
    private final NotesRepo notesRepo;

    private final ArrayList<Note> _notes = new ArrayList<>();
    MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    MutableLiveData<State> state = new MutableLiveData<>();

    private Disposable dataDisposable;

    public NoteListViewModel(@NonNull Application application) {
        super(application);

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        authRepo = NotefyApplication.get(application).getAuthRepo();

        dataDisposable = authRepo.getSessionObservable()
                .doOnSubscribe(disposable -> {
                    setState(State.Loading);
                })
                .doOnNext(auth -> {
                    if (!auth) {
                        _notes.clear();
                        notesRepo.detachListener();
                        setState(State.Unauthorized);
                    } else {
                        notesRepo.attachListener();
                        setState(State.Empty);
                    }
                })
                .filter(auth -> auth)
                .switchMap(auth -> notesRepo.notes)
                .subscribe(this::onNoteEvent);
    }

    private void setState(State state) {
        this.state.setValue(state);
    }

//    private void subscribeNotes() {
//        notesDisposable = notesRepo.notes.subscribe(this::onNoteEvent);
//    }
//
//    private void unsubscribeNotes() {
//        if (notesDisposable != null) {
//            notesDisposable.dispose();
//            notesDisposable = null;
//        }
//    }
//
//    private void onSessionStatusChanged(@NonNull Boolean hasSession) {
//        if (hasSession) {
//            setState(State.Empty);
//            subscribeNotes();
//        } else {
//            unsubscribeNotes();
//            _notes.clear();
//            setState(State.Unauthorized);
//        }
//    }

    private void onNoteEvent(NotesRepo.Event event) {
        if (event instanceof NotesRepo.Event.Added) {
            Note note = ((NotesRepo.Event.Added) event).data();
            onNoteAdded(note);
        } else if (event instanceof NotesRepo.Event.Changed) {
            Note note = ((NotesRepo.Event.Changed) event).data();
            onNoteChanged(note);
        } else if (event instanceof NotesRepo.Event.Removed) {
            Note note = ((NotesRepo.Event.Removed) event).data();
            onNoteRemoved(note);
        } else {
            Timber.w("unhandled note event => %s", event.getClass().getSimpleName());
        }
    }

    private void onNoteAdded(@NonNull Note note) {
        _notes.add(note);
        notes.setValue(_notes);

        setState(State.Data);
    }

    private void onNoteChanged(@NonNull Note note) {
        int index = _notes.indexOf(note);
        if (index == -1) {
            Timber.w("note not found");
            return;
        }

        _notes.set(index, note);
        notes.setValue(_notes);

        setState(State.Data);
    }

    private void onNoteRemoved(@NonNull Note note) {
        int index = _notes.indexOf(note);
        if (index == -1) {
            Timber.w("note not found");
            return;
        }

        _notes.remove(index);
        notes.setValue(_notes);

        if (_notes.size() == 0) {
            setState(State.Empty);
        } else {
            setState(State.Data);
        }
    }

    @Override
    protected void onCleared() {
//        unsubscribeNotes();
        if (dataDisposable != null) {
            dataDisposable.dispose();
        }
        super.onCleared();
    }
}
