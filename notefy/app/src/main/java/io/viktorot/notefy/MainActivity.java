package io.viktorot.notefy;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainMenuDialog.Callback {

    private final MainMenuDialog menu = new MainMenuDialog();

    private BottomAppBar appBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBar = findViewById(R.id.app_bar);
        appBar.setNavigationOnClickListener(view -> {
            openMenu();
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
        });
    }

    private void openMenu() {
        menu.setCallback(this);
        menu.show(getSupportFragmentManager(), MainMenuDialog.TAG);
    }

    @Override
    public void login() {
        Toast.makeText(this, "1111", Toast.LENGTH_SHORT).show();
    }
}
