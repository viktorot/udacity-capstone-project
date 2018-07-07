package io.viktorot.notefy;

import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final MainMenu menu = new MainMenu();

    private BottomAppBar appBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBar = findViewById(R.id.app_bar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            openMenu();
        });
    }

    private void openMenu() {
        menu.show(getSupportFragmentManager(), MainMenu.TAG);
    }
}
