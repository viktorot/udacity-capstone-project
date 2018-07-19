package io.viktorot.notefy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.viktorot.notefy.navigator.Command;
import io.viktorot.notefy.navigator.Pop;
import io.viktorot.notefy.navigator.Push;
import timber.log.Timber;

public abstract class FragmentNavigator {

    protected boolean attached = false;

    private final FragmentManager fragmentManager;

    private final int containerId;

    protected LinkedList<String> localStackCopy;

    private LinkedList<Command[]> pendingCommands = new LinkedList<>();

    public FragmentNavigator(@NonNull FragmentManager fragmentManager, @IdRes int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;

        copyStackToLocal();
    }

    public void attach() {
        attached = true;
        while (pendingCommands.size() != 0) {
            applyCommands(pendingCommands.poll());
        }
    }

    public void detach() {
        attached = false;
    }


    public void applyCommand(Command command) {
        applyCommands(new Command[] { command });
    }

    public void applyCommands(Command[] commands) {
        if (!attached) {
            pendingCommands.add(commands);
            return;
        }

        fragmentManager.executePendingTransactions();

        copyStackToLocal();

        for (Command command : commands) {
            _applyCommand(command);
        }
    }

    protected void _applyCommand(Command command) {
        if (command instanceof Push) {
             push((Push) command);
        } else if (command instanceof Pop) {
           pop((Pop) command);
        }
    }

    private void copyStackToLocal() {
        localStackCopy = new LinkedList<>();

        final int stackSize = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < stackSize; i++) {
            localStackCopy.add(fragmentManager.getBackStackEntryAt(i).getName());
        }
    }

    protected void push(Push command) {
        Fragment fragment = command.getFragment();
        String tag = command.getTag();

        Objects.requireNonNull(fragment);
        Objects.requireNonNull(tag);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction
                .replace(containerId, fragment)
                .addToBackStack(tag)
                .commit();

        localStackCopy.push(tag);
    }

    protected void pop(Pop command) {
        if (localStackCopy.size() != 0) {
            String tag = localStackCopy.getFirst();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);

            fragmentManager.popBackStack();

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
            } else {
                Timber.v("fragment with tag => %s not found. skipping remove", tag);
            }

            localStackCopy.pop();

        } else {
            exit();
        }
    }

    public abstract void exit();
}
