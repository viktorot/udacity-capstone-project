package io.viktorot.notefy.repo;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxrelay2.BehaviorRelay;

import io.reactivex.Observable;
import timber.log.Timber;

public class AuthRepo {

    private final FirebaseAuth auth;
    private final FirebaseAuth.AuthStateListener listener;

    private final BehaviorRelay<Boolean> session = BehaviorRelay.create();
    //public final MutableLiveData<Boolean> session = new MutableLiveData<>();

    @Nullable
    private FirebaseUser user = null;

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

    public void logout() {
        this.auth.signOut();
    }

    public Observable<Boolean> getSessionObservable() {
        return session.distinctUntilChanged();
    }

    public boolean hasSession() {
        Boolean val = this.session.getValue();
        return val != null && val;
    }

    @Nullable
    public String getUserEmail() {
        if (this.user != null) {
            return this.user.getEmail();
        } else {
            return null;
        }
    }

    @Nullable
    public String getUserDisplayName() {
        if (this.user != null) {
            return this.user.getDisplayName();
        } else {
            return null;
        }
    }

    private void onAuthStateChanged(FirebaseAuth auth) {
        this.user = auth.getCurrentUser();
        boolean hasSession = this.user != null;
        Timber.d("has session => %b", hasSession);
        session.accept(hasSession);
    }
}
