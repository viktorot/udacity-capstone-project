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

class NoteListViewModel extends AndroidViewModel {

    private final NotesRepo notesRepo;

    private final ArrayList<Note> _notes = new ArrayList<>();
    MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    private Disposable notesDisposable;

    public NoteListViewModel(@NonNull Application application) {
        super(application);

        notesRepo = NotefyApplication.get(application).getNotesRepo();

        notesDisposable = notesRepo.notes.subscribe(this::onNoteAdded);
    }

    private void onNoteAdded(@NonNull Note note) {
        _notes.add(note);
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
