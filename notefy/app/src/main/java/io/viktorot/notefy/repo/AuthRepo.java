package io.viktorot.notefy.repo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class AuthRepo {

    private final FirebaseAuth auth;
    private final FirebaseAuth.AuthStateListener listener;

    public final MutableLiveData<Boolean> session = new MutableLiveData<>();

    public AuthRepo(FirebaseAuth auth) {
        this.auth = auth;
        this.listener = this::onAuthStateChanged;
    }

    public void attachListener() {
        this.auth.addAuthStateListener(this.listener);
    }

    public void detachListener() {
        this.auth.removeAuthStateListener(this.listener);
    }

    public boolean hasSession() {
        Boolean val = this.session.getValue();
        return val != null && val;
    }

    private void onAuthStateChanged(FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();
        boolean hasSession = user != null;
        Timber.d("has session => %b", hasSession);
        session.setValue(hasSession);
    }
}
