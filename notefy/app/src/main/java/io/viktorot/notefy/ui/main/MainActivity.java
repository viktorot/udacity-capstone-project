package io.viktorot.notefy.ui.main;

import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.viktorot.notefy.FragmentNavigator;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;

public class MainActivity extends AppCompatActivity {

    private FragmentNavigator fragmentNavigator;

    private MainViewModel vm;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            vm.newNote();
        });
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        fragmentNavigator.attach();

        NotefyApplication.get(this).getNavigator()
                .attach(this, getSupportFragmentManager(), R.id.fragment_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotefyApplication.get(this).getAuthRepo().attachListener();
    }

    @Override
    protected void onPause() {
        //NotefyApplication.get(this).getAuthRepo().detachListener();
        fragmentNavigator.detach();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        NotefyApplication.get(this).getNavigator().detach();
        super.onStop();
    }

    private void onViewModelAction(MainViewModel.Action action) {
        if (action == MainViewModel.Action.ShowLoginMenu) {
            openLoginMenu();
        } else if (action == MainViewModel.Action.ShowAppMenu) {
            openAppMenu();
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
}
