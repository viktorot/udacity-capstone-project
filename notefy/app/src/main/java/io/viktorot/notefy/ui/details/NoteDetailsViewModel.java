package io.viktorot.notefy.ui.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.viktorot.notefy.Navigator;
import io.viktorot.notefy.NotefyApplication;

public class NoteDetailsViewModel extends AndroidViewModel {

    private final Navigator navigator;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        navigator = NotefyApplication.get(application).getNavigator();
    }

    void back() {
        navigator.back();
    }
}
