package io.viktorot.notefy.repo;

import com.jakewharton.rxrelay2.PublishRelay;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FilterRelay {

    private PublishRelay<Integer> relay = PublishRelay.create();

    public Disposable observe(Consumer<Integer> consumer) {
        return relay.subscribe(relay);
    }

    public void post(Integer value) {
        relay.accept(value);
    }
}
