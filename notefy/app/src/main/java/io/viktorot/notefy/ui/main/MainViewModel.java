package io.viktorot.notefy.ui.main;

import android.app.Application;

import com.google.auto.value.AutoValue;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.repo.AuthRepo;
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
    private final Navigator navigator;

    final SingleLiveEvent<Action> actions = new SingleLiveEvent<>();

    private final Observer<Boolean> sessionObserver = hasSession -> {
        if (hasSession == null) {
            return;
        }
        onSessionStatusChanged(hasSession);
    };

    MainViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        authRepo = NotefyApplication.get(application).getAuthRepo();
        authRepo.session.observeForever(this::onSessionStatusChanged);
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

    void newNote() {
        // TODO: navigate to new note
    }

    private void onSessionStatusChanged(@NonNull Boolean hasSession) {
        Timber.v("--- => %b", hasSession);
    }

    @Override
    protected void onCleared() {
        authRepo.session.removeObserver(sessionObserver);
        super.onCleared();
    }
}
