package io.viktorot.notefy;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.LinkedList;
import java.util.Objects;

import io.viktorot.notefy.navigator.events.NavEvent;
import io.viktorot.notefy.navigator.events.Pop;
import io.viktorot.notefy.navigator.events.Push;
import io.viktorot.notefy.navigator.events.Replace;
import timber.log.Timber;

public abstract class FragmentNavigator {

    protected boolean attached = false;

    private final FragmentManager fragmentManager;

    private final int containerId;
    private final int baseContainerId;

    protected LinkedList<String> localStackCopy;

    private LinkedList<NavEvent[]> pendingCommands = new LinkedList<>();

    public FragmentNavigator(@NonNull FragmentManager fragmentManager, @IdRes int baseContainerId, @IdRes int containerId) {
        this.fragmentManager = fragmentManager;
        this.baseContainerId = baseContainerId;
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


    public void applyCommand(NavEvent command) {
        applyCommands(new NavEvent[]{command});
    }

    public void applyCommands(NavEvent[] commands) {
        if (!attached) {
            pendingCommands.add(commands);
            return;
        }

        fragmentManager.executePendingTransactions();

        copyStackToLocal();

        for (NavEvent command : commands) {
            _applyCommand(command);
        }
    }

    protected void _applyCommand(NavEvent command) {
        if (command instanceof Push) {
            push((Push) command);
        } else if (command instanceof Pop) {
            pop((Pop) command);
        } else if (command instanceof Replace) {
            replace((Replace) command);
        }
    }

    private void copyStackToLocal() {
        localStackCopy = new LinkedList<>();

        final int stackSize = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < stackSize; i++) {
            localStackCopy.add(fragmentManager.getBackStackEntryAt(i).getName());
        }
    }

    protected void replace(Replace command) {
        if (localStackCopy.size() > 0) {
            fragmentManager.popBackStack();
            String prevTag = localStackCopy.pop();

            Timber.d("replacing => %s with => %s", prevTag, command.getTag());

            fragmentManager
                    .beginTransaction()
                    .replace(baseContainerId, command.getFragment(), command.getTag())
                    .addToBackStack(command.getTag())
                    .commit();

            localStackCopy.push(command.getTag());
        } else {
            Timber.d("replacing => %s", command.getTag());

            fragmentManager.beginTransaction()
                    .replace(baseContainerId, command.getFragment(), command.getTag())
                    .commit();
        }
    }

    protected void push(Push command) {
        Fragment fragment = command.getFragment();
        String tag = command.getTag();

        Objects.requireNonNull(fragment);
        Objects.requireNonNull(tag);

        Timber.d("pushing => %s", command.getTag());

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.enter, R.animator.exit,
                        R.animator.enter, R.animator.exit)
                .add(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit();

        localStackCopy.push(tag);
    }

    protected void pop(Pop command) {
        if (localStackCopy.size() != 0) {
            String tag = localStackCopy.getFirst();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);

            if (!command.isForce() && fragment instanceof Navigatable && !((Navigatable) fragment).onBackPressed()) {
                return;
            }

            fragmentManager.popBackStack();

            Timber.d("poping => %s", tag);

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
            } else {
                Timber.v("fragment with tag => %s not found. skipping remove", tag);
            }

            localStackCopy.pop();

        } else {
            Timber.v("exiting...");
            exit();
        }
    }

    public int getBackstackSize() {
        return localStackCopy.size();
    }

    public abstract void exit();
}
