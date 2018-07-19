package io.viktorot.notefy.ui.details;

import android.app.Application;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.util.SingleLiveEvent;
import timber.log.Timber;

public class NoteDetailsViewModel extends AndroidViewModel {

    private final Navigator navigator;

    enum Action {
        SelectIcon
    }

    SingleLiveEvent<Action> action = new SingleLiveEvent<>();

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();
    }

    private void dispatchAction(Action action) {
        this.action.setValue(action);
    }

    void selectIcon() {
        dispatchAction(Action.SelectIcon);
    }

    void back() {
        navigator.back();
    }

    void onIconSelected(@DrawableRes int iconId) {
        Timber.d("icon selected => %d", iconId);
    }
}
