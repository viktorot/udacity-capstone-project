package io.viktorot.notefy.ui.list;

import android.app.Application;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
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
import io.viktorot.notefy.util.NotificationUtils;
import timber.log.Timber;

public class NoteListViewModel extends AndroidViewModel {

    enum State {
        Loading, Empty, Data
    }

    private final Navigator navigator;

    private final AuthRepo authRepo;
    private final NotesRepo notesRepo;

    private final NotificationUtils notificationUtils;

    private final ArrayList<Note> _notes = new ArrayList<>();
    MutableLiveData<List<NoteListViewItem>> notes = new MutableLiveData<>();

    private final FilterRelay filterRelay;

    MutableLiveData<State> state = new MutableLiveData<>();

    private Disposable dataDisposable;

    public NoteListViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        notesRepo = NotefyApplication.get(application).getNotesRepo();
        authRepo = NotefyApplication.get(application).getAuthRepo();

        filterRelay = NotefyApplication.get(application).getFilterRelay();

        notificationUtils = NotefyApplication.get(application).getNotificationUtils();

        dataDisposable = authRepo.getSessionObservable()
                .doOnSubscribe(disposable -> {
                    setState(State.Loading);
                })
                .doOnNext(auth -> {
                    if (!auth) {
                        _notes.clear();
                        notes.setValue(Collections.emptyList());
                        notesRepo.clearCache();
                        notesRepo.detachListener();
                        notificationUtils.removeAll();
                        setState(State.Loading);
                    } else {
                        notesRepo.attachListener();
                        setState(State.Loading);
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
                .subscribe(notes -> {
                    onNotesReceived(notes);
                    updateNotifications(notes);
                });
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

        List<NoteListViewItem> oldItems = this.notes.getValue();

        if (oldItems == null) {
            ArrayList<NoteListViewItem> viewItems = new ArrayList<>();
            for (Note note : notes) {
                viewItems.add(new NoteListViewItem(getApplication(), note));
            }
            this.notes.setValue(viewItems);
        } else {
            ArrayList<NoteListViewItem> latestItems = new ArrayList<>();
            for (Note note : notes) {
                int found = -1;
                for (int i = 0; i < oldItems.size(); i++) {
                    NoteListViewItem oldItem = oldItems.get(0);
                    if (oldItem.getData().equals(note)) {
                        found = i;
                        break;
                    }
                }

                if (found > -1) {
                    latestItems.add(oldItems.get(found));
                } else {
                    latestItems.add(new NoteListViewItem(getApplication(), note));
                }
            }
            this.notes.setValue(latestItems);
        }

        if (_notes.size() == 0) {
            setState(State.Empty);
        } else {
            setState(State.Data);
        }
    }

    private void updateNotifications(@NonNull List<Note> notes) {
        for (Note note : notes) {
            notificationUtils.notify(note);
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
