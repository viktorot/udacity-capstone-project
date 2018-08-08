package io.viktorot.notefy.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.AuthRepo;
import io.viktorot.notefy.repo.FilterRelay;
import io.viktorot.notefy.util.SingleLiveEvent;

public class MainViewModel extends AndroidViewModel {

    enum Action {
        ShowLoginMenu,
        ShowAppMenu,
        ShowFilterDialog,
        ShowUnauthorizedMessage
    }

    enum State {
        Unauthorized, Authorized
    }

    private final AuthRepo authRepo;

    private final FilterRelay filterRelay;

    private final Navigator navigator;

    final MutableLiveData<State> state = new MutableLiveData<>();
    final SingleLiveEvent<Action> actions = new SingleLiveEvent<>();

    private Disposable authDisposable;

    public MainViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();

        filterRelay = NotefyApplication.get(application).getFilterRelay();

        authRepo = NotefyApplication.get(application).getAuthRepo();

        authDisposable = authRepo.getSessionObservable()
                .subscribe(auth -> {
                    if (auth) {
                        state.setValue(State.Authorized);
                    } else {
                        state.setValue(State.Unauthorized);
                    }
                });
    }

    private void dispatchAction(@NonNull Action action) {
        this.actions.setValue(action);
    }

    @NonNull
    String getActiveColorFilter() {
        return filterRelay.getActiveColorFilter();
    }

    int getActiveTagFilter() {
        return filterRelay.getActiveTagFilter();
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

    void filter() {
        dispatchAction(Action.ShowFilterDialog);
    }

    void newNote() {
//        if (!authRepo.hasSession()) {
//            dispatchAction(Action.ShowUnauthorizedMessage);
//            return;
//        }
        navigator.navigateToNewNote();
    }

    void editNote(@NonNull Note note) {
        navigator.navigateToEditNote(note);
    }

    void onColorFilterSelected(@NonNull String color) {
        filterRelay.postColor(color);
    }

    void onTagFilterSelected(int id) {
        filterRelay.postTag(id);
    }

    @Override
    protected void onCleared() {
        if (authDisposable != null) {
            authDisposable.dispose();
        }
        super.onCleared();
    }
}
