package io.viktorot.notefy.util;

import android.support.annotation.NonNull;

import com.jakewharton.rxrelay2.BehaviorRelay;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class ConnectionRelay {

    private final BehaviorRelay<Boolean> relay = BehaviorRelay.create();

    @NonNull
    public Disposable observe(Consumer<Boolean> consumer) {
        return relay
                .distinctUntilChanged()
                .subscribe(consumer);
    }

    public void post(boolean connected) {
        Timber.d("internet connection => %b", connected);
        relay.accept(connected);
    }
}
