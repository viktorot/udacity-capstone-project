package io.viktorot.notefy;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.navigator.events.Login;
import io.viktorot.notefy.navigator.events.NavEvent;
import io.viktorot.notefy.navigator.events.Back;
import io.viktorot.notefy.navigator.events.Pop;
import io.viktorot.notefy.navigator.events.Push;
import io.viktorot.notefy.ui.details.NoteDetailsFragment;

public class Navigator {

    private NavEventRelay relay;

    public Navigator(NavEventRelay relay) {
        this.relay = relay;
    }

    public Disposable observe(Consumer<NavEvent> consumer) {
        return this.relay.observe(consumer);
    }

    public void navigateToLogin() {
        relay.post(new Login());
    }

    public void navigateToNewNote() {
        navigateToDetails(Note.empty());
    }

    public void navigateToEditNote(@NonNull Note note) {
        navigateToDetails(Note.copy(note));
    }

    private void navigateToDetails(@NonNull Note note) {
        NoteDetailsFragment fragment = NoteDetailsFragment.newInstance(note);
        relay.post(new Push(fragment, NoteDetailsFragment.TAG));
    }

    public void back() {
        relay.post(new Back());
    }

    public void pop() {
        relay.post(new Pop());
    }
}
