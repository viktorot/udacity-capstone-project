package io.viktorot.notefy.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.FragmentNavigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.navigator.events.Login;
import io.viktorot.notefy.navigator.events.NavEvent;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int RESULT_CODE_LOGIN = 1;

    private FragmentNavigator fragmentNavigator;

    private MainViewModel vm;

    private Disposable navigationDisposable;

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

        fragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), R.id.fragment_container) {
            @Override
            public void exit() {
                finish();
            }
        };

        vm = ViewModelProviders.of(this).get(MainViewModel.class);
        vm.actions.observe(this, this.actionsObserver);

        BottomAppBar appBar = findViewById(R.id.app_bar);
        appBar.setNavigationOnClickListener(view -> {
            vm.menu();
        });
        appBar.inflateMenu(R.menu.main);
        appBar.setOnMenuItemClickListener(this::onMenuItemSelected);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            vm.newNote();
        });
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        fragmentNavigator.attach();

        navigationDisposable = NotefyApplication.get(this)
                .getNavigator()
                .observe(this::onNavEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotefyApplication.get(this).getAuthRepo().attachListener();
    }

    @Override
    protected void onPause() {
        fragmentNavigator.detach();
        if (navigationDisposable != null) {
            navigationDisposable.dispose();
        }
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

    private void onViewModelAction(MainViewModel.Action action) {
        if (action == MainViewModel.Action.ShowLoginMenu) {
            openLoginMenu();
        } else if (action == MainViewModel.Action.ShowAppMenu) {
            openAppMenu();
        } else if (action == MainViewModel.Action.ShowFilterDialog) {
            showFilterDialog();
        }
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
