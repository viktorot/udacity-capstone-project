package io.viktorot.notefy.ui.main;

import android.app.Application;

import com.google.auto.value.AutoValue;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.NotesRepo;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {

    enum Action {
        ShowLoginMenu,
        ShowAppMenu
    }

    private final AuthRepo authRepo;
    private final NotesRepo notesRepo;

    private final Navigator navigator;

    final SingleLiveEvent<Action> actions = new SingleLiveEvent<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        authRepo = NotefyApplication.get(application).getAuthRepo();
        notesRepo = NotefyApplication.get(application).getNotesRepo();
    }

    private void dispatchAction(@NonNull Action action) {
        this.actions.setValue(action);
    }

    void menu() {
        if (authRepo.hasSession()) {
            dispatchAction(Action.ShowAppMenu);
        } else {
            dispatchAction(Action.ShowLoginMenu);
        }
    }

    void login() {
        navigator.navigateToLogin();
    }

    void logout() {
        authRepo.logout();
    }

    private int count = 0;

    void newNote() {
        count++;
        notesRepo.save(new Note("title " + count, "content"));
        Timber.d("adding note => %d", count);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
