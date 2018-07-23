package io.viktorot.notefy.ui.list;

import android.app.Application;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.FilterRelay;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.repo.TagRepo;
import timber.log.Timber;

public class NoteListViewModel extends AndroidViewModel {

    enum State {
        Unauthorized, Loading, Empty, Data
    }

    private final Navigator navigator;

    private final AuthRepo authRepo;
    private final NotesRepo notesRepo;

    private final ArrayList<Note> _notes = new ArrayList<>();
    MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    private final FilterRelay filterRelay;

    MutableLiveData<State> state = new MutableLiveData<>();

    private Disposable dataDisposable;

    public NoteListViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        authRepo = NotefyApplication.get(application).getAuthRepo();

        filterRelay = NotefyApplication.get(application).getFilterRelay();

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
                .switchMap((Function<Boolean, ObservableSource<List<Note>>>) auth ->
                        Observable.combineLatest(filterRelay.getColorFilterObservable(), filterRelay.getTagFilterObservable(), notesRepo.notes, (color, tagId, notes) -> {
                            Timber.d("filter color => %s, tag => %d", color, tagId);
                            ArrayList<Note> step1 = new ArrayList<>();

                            if (TextUtils.isEmpty(color)) {
                                step1.addAll(notes);
                            } else {
                                for (Note note : notes) {
                                    if (note.getColor().equals(color)) {
                                        step1.add(note);
                                    }
                                }
                            }

                            ArrayList<Note> step2 = new ArrayList<>();

                            if (tagId == TagRepo.ID_NONE) {
                                step2.addAll(step1);
                            } else {
                                for (Note note : step1) {
                                    if (note.getTagId() == tagId) {
                                        step2.add(note);
                                    }
                                }
                            }

                            return step2;
                        }))


//                .switchMap((Function<Boolean, ObservableSource<List<Note>>>) auth ->
//                        // TODO: test switchMap vs flatMap
//                        filterRelay.getColorFilterObservable().flatMap(new Function<String, ObservableSource<List<Note>>>() {
//                            @Override
//                            public ObservableSource<List<Note>> apply(String s) {
//                                return notesRepo.notes;
//                            }
//                        }))
                .subscribe(this::onNotesReceived);
    }

    private void setState(State state) {
        this.state.setValue(state);
    }

    void editNote(@NonNull Note note) {
        navigator.navigateToEditNote(note);
    }

    private void onNotesReceived(@NonNull List<Note> notes) {
        _notes.clear();
        _notes.addAll(notes);

        if (_notes.size() == 0) {
            setState(State.Empty);
        } else {
            setState(State.Data);
        }

        this.notes.setValue(notes);
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
        if (dataDisposable != null) {
            dataDisposable.dispose();
        }
        super.onCleared();
    }
}
