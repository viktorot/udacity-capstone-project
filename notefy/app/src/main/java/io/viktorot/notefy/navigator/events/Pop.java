package io.viktorot.notefy.navigator.events;

public class Pop implements NavEvent {

    private boolean force = false;

    public Pop() {
        this.force = false;
    }

    public Pop(boolean force) {
        this.force = force;
    }

    public boolean isForce() {
        return force;
    }
}
