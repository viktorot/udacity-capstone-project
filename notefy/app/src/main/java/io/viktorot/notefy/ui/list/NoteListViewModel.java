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
import io.viktorot.notefy.repo.NotesRepo;
import timber.log.Timber;

class NoteListViewModel extends AndroidViewModel {

    private final NotesRepo notesRepo;

    private final ArrayList<Note> _notes = new ArrayList<>();
    MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    private Disposable notesDisposable;

    public NoteListViewModel(@NonNull Application application) {
        super(application);

        notesRepo = NotefyApplication.get(application).getNotesRepo();

        notesDisposable = notesRepo.notes.subscribe(this::onNoteEvent);
    }

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
    }

    private void onNoteChanged(@NonNull Note note) {
        int index = _notes.indexOf(note);
        if (index == -1) {
            Timber.w("note not found");
            return;
        }

        _notes.set(index, note);
        notes.setValue(_notes);
    }

    private void onNoteRemoved(@NonNull Note note) {
        int index = _notes.indexOf(note);
        if (index == -1) {
            Timber.w("note not found");
            return;
        }

        _notes.remove(index);
        notes.setValue(_notes);
    }

    @Override
    protected void onCleared() {
        if (notesDisposable != null) {
            notesDisposable.dispose();
        }
        super.onCleared();
    }
}
