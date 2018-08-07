package io.viktorot.notefy.util;

import com.jakewharton.rxrelay2.BehaviorRelay;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.viktorot.notefy.BuildConfig;
import timber.log.Timber;

public class ConnectionRelay {

    private final BehaviorRelay<Boolean> relay = BehaviorRelay.create();

    @NonNull
    public Disposable observe(Consumer<Boolean> consumer) {
//        if (BuildConfig.DEBUG) {
//            return relay.subscribe(consumer);
//        }

        return relay
                .distinctUntilChanged()
                .subscribe(consumer);
    }

    public void post(boolean connected) {
        Timber.d("internet connection => %b", connected);
        relay.accept(connected);
    }
}
