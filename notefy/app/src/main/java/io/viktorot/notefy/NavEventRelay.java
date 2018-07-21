package io.viktorot.notefy;

import com.jakewharton.rxrelay2.PublishRelay;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.viktorot.notefy.navigator.events.NavEvent;

public class NavEventRelay {

    private PublishRelay<NavEvent> relay = PublishRelay.create();

    public Disposable observe(Consumer<NavEvent> consumer) {
        return relay.subscribe(consumer);
    }

    public void post(@NonNull NavEvent event)  {
        relay.accept(event);
    }
}
