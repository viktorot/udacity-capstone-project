package io.viktorot.notefy.ui.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.FragmentNavigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.navigator.events.Login;
import io.viktorot.notefy.navigator.events.NavEvent;
import io.viktorot.notefy.util.NotificationUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int RESULT_CODE_LOGIN = 1;

    private FragmentNavigator fragmentNavigator;

    private MainViewModel vm;

    private MenuItem filterMenuItem;

    private Disposable navigationDisposable;

    private final Observer<MainViewModel.State> stateObserver = state -> {
        if (state == null) {
            return;
        }
        onViewModelStateChanged(state);
    };

    private final Observer<MainViewModel.Action> actionsObserver = action -> {
        if (action == null) {
            return;
        }
        onViewModelAction(action);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.notes_notification_channel_id);
            String channelName = getString(R.string.notes_notification_channel_name);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));

        }

        fragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), R.id.fragment_container) {
            @Override
            public void exit() {
                finish();
            }
        };

        BottomAppBar appBar = findViewById(R.id.app_bar);
        appBar.setNavigationOnClickListener(view -> {
            vm.menu();
        });
        appBar.inflateMenu(R.menu.main);
        appBar.setOnMenuItemClickListener(this::onMenuItemSelected);

        filterMenuItem = appBar.getMenu().getItem(0);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            vm.newNote();
        });

        vm = ViewModelProviders.of(this).get(MainViewModel.class);
        vm.state.observe(this, this.stateObserver);
        vm.actions.observe(this, this.actionsObserver);

        navigationDisposable = NotefyApplication.get(this)
                .getNavigator()
                .observe(this::onNavEvent);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Note note = intent.getParcelableExtra(NotificationUtils.NOTE_DATA);
        if (note == null) {
            Timber.w("notification note null");
            return;
        }
        vm.editNote(note);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        fragmentNavigator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotefyApplication.get(this).getAuthRepo().attachListener();
    }

    @Override
    protected void onPause() {
        fragmentNavigator.detach();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (navigationDisposable != null) {
            navigationDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        NotefyApplication.get(this)
                .getNavigator().back();
    }

    private boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_filter) {
            vm.filter();
            return true;
        }
        return false;
    }

    private void onNavEvent(NavEvent event) {
        if (event instanceof Login) {
            openLoginActivity();
            return;
        }
        fragmentNavigator.applyCommand(event);
    }

    private void onViewModelStateChanged(MainViewModel.State state) {
        if (state == MainViewModel.State.Authorized) {
            showListFragment();
            filterMenuItem.setVisible(true);
        } else if (state == MainViewModel.State.Unauthorized) {
            hideListFragment();
            filterMenuItem.setVisible(false);
        }
    }

    private void onViewModelAction(MainViewModel.Action action) {
        if (action == MainViewModel.Action.ShowLoginMenu) {
            openLoginMenu();
        } else if (action == MainViewModel.Action.ShowAppMenu) {
            openAppMenu();
        } else if (action == MainViewModel.Action.ShowFilterDialog) {
            showFilterDialog();
        } else if (action == MainViewModel.Action.ShowUnauthorizedMessage) {
            Toast.makeText(this, "[you are not logged in]", Toast.LENGTH_LONG).show();
        }
    }

    private void showListFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.note_list_fragment);

        if (fragment == null) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commit();
    }

    private void hideListFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.note_list_fragment);

        if (fragment == null) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .hide(fragment)
                .commit();
    }

    private void openLoginMenu() {
        LoginMenuDialog.Builder.create()
                .setCallback(vm::login)
                .show(getSupportFragmentManager());
    }

    private void openAppMenu() {
        AppMenuDialog.Builder.create()
                .setCallback(vm::logout)
                .show(getSupportFragmentManager());
    }

    private void showFilterDialog() {
        FilterDialog.Builder.create()
                .setSelectedColor(vm.getActiveColorFilter())
                .setSelectedTagId(vm.getActiveTagFilter())
                .setCallback(new FilterDialog.Callback() {
                    @Override
                    public void onColorSelected(@NonNull String color) {
                        vm.onColorFilterSelected(color);
                    }

                    @Override
                    public void onTagSelected(int id) {
                        vm.onTagFilterSelected(id);
                    }
                })
                .show(getSupportFragmentManager());
    }

    private void openLoginActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RESULT_CODE_LOGIN);
    }
}
