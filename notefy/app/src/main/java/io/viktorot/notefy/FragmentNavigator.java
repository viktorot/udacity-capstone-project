package io.viktorot.notefy;

import java.util.LinkedList;
import java.util.Objects;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.navigator.events.NavEvent;
import io.viktorot.notefy.navigator.events.Back;
import io.viktorot.notefy.navigator.events.Pop;
import io.viktorot.notefy.navigator.events.Push;
import timber.log.Timber;

public abstract class FragmentNavigator {

    protected boolean attached = false;

    private final FragmentManager fragmentManager;

    private final int containerId;

    protected LinkedList<String> localStackCopy;

    private LinkedList<NavEvent[]> pendingCommands = new LinkedList<>();

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
        } else if (command instanceof Back) {
            back((Back) command);
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

        Timber.d("pushing => %s", command.getTag());

        fragmentManager.beginTransaction()
                .add(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit();

        localStackCopy.push(tag);
    }

    protected void back(Back command) {
        if (localStackCopy.size() != 0) {
            String tag = localStackCopy.getFirst();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);

            if (fragment instanceof Navigatable && !((Navigatable) fragment).onBackPressed()) {
                return;
            }

            Timber.d("poping => %s", tag);

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
