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

class MainViewModel extends AndroidViewModel {

    static abstract class Action {
        @AutoValue
        abstract static class ShowLoginMenu extends Action {
            static Action.ShowLoginMenu create() {
                return new AutoValue_MainViewModel_Action_ShowLoginMenu();
            }
        }

        @AutoValue
        abstract static class ShowAppMenu extends Action {
            static Action.ShowAppMenu create() {
                return new AutoValue_MainViewModel_Action_ShowAppMenu();
            }
        }
    }

    private final AuthRepo authRepo;
    private final NotesRepo notesRepo;

    private final Navigator navigator;

    final SingleLiveEvent<Action> actions = new SingleLiveEvent<>();

    private Disposable sessionDisposable;

    MainViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        authRepo = NotefyApplication.get(application).getAuthRepo();
        notesRepo = NotefyApplication.get(application).getNotesRepo();

        sessionDisposable = authRepo.session.subscribe(this::onSessionStatusChanged);
    }

    private void dispatchAction(@NonNull Action action) {
        this.actions.setValue(action);
    }

    void menu() {
        if (authRepo.hasSession()) {
            dispatchAction(Action.ShowAppMenu.create());
        } else {
            dispatchAction(Action.ShowLoginMenu.create());
        }
    }

    void login() {
        navigator.navigateToLogin();
    }

    void logout() {
        authRepo.logout();
    }

    void newNote() {
        notesRepo.save(new Note("title", "content"));
    }

    private void onSessionStatusChanged(@NonNull Boolean hasSession) {
        Timber.v("--- => %b", hasSession);
    }

    @Override
    protected void onCleared() {
        if (sessionDisposable != null) {
            sessionDisposable.dispose();
        }
        super.onCleared();
    }
}
