package io.viktorot.notefy.repo;

import com.jakewharton.rxrelay2.PublishRelay;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FilterRelay {

    private PublishRelay<String> relay = PublishRelay.create();

    public Observable<String> getObservable() {
        return relay.startWith("");
    }

    public Disposable observe(Consumer<String> consumer) {
        return relay.subscribe(relay);
    }

    public void post(String value) {
        relay.accept(value);
    }
}
