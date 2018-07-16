package io.viktorot.notefy;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.viktorot.notefy.ui.list.NoteListFragment;
import io.viktorot.notefy.ui.main.MainActivity;
import io.viktorot.notefy.ui.note.NoteFragment;

public class Navigator {

    public static final int RESULT_CODE_LOGIN = 1;

    private MainActivity activity;
    private FragmentManager fragmentManager;
    private int container;

    public void attach(@NonNull MainActivity activity, @NonNull FragmentManager fragmentManager, @IdRes int container) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.container = container;
    }

    public void detach() {
        this.activity = null;
        this.fragmentManager = null;
    }

    public void navigateToLogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RESULT_CODE_LOGIN);
    }

    public void navigateToDetails() {
        NoteFragment fragment = NoteFragment.newInstance();

        fragmentManager.beginTransaction()
                .add(container, fragment, NoteFragment.TAG)
                .commit();
    }

    public void back() {
        Fragment fragment = fragmentManager.findFragmentByTag(NoteFragment.TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        fragmentManager.popBackStack();
    }
}
