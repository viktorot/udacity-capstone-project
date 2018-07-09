package io.viktorot.notefy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainMenuDialog extends BottomSheetDialogFragment {

    public static final String TAG = MainMenuDialog.class.getSimpleName();

    @Nullable
    private MainMenuDialog.Callback callback;

    public void setCallback(@NonNull MainMenuDialog.Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_menu_main, container, false);

        Button loginBtn = view.findViewById(R.id.login);
        loginBtn.setOnClickListener(view1 -> onLoginClick());

        return view;
    }

    @Override
    public void onDestroyView() {
        this.callback = null;
        super.onDestroyView();
    }

    private void onLoginClick() {
        if (MainMenuDialog.this.callback != null) {
            MainMenuDialog.this.callback.login();
        }
        dismiss();
    }

    public interface Callback {
        void login();
    }
}
