package io.viktorot.notefy;

import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainMenuDialog.Callback {

    private final MainMenuDialog menu = new MainMenuDialog();

    public static final int RC_SIGN_IN = 1;

    private BottomAppBar appBar;
    private FloatingActionButton fab;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Toast.makeText(MainActivity.this, "yayy", Toast.LENGTH_LONG).show();
            } else {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        };

        appBar = findViewById(R.id.app_bar);
        appBar.setNavigationOnClickListener(view -> {
            openMenu();
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onPause() {
        auth.removeAuthStateListener(authListener);
        super.onPause();
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
