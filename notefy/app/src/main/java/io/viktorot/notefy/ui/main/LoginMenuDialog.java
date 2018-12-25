package io.viktorot.notefy.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;

public class LoginMenuDialog extends NotefyBottomSheetDialogFragment  {

    private static final String TAG = LoginMenuDialog.class.getSimpleName();

    @Nullable
    private LoginMenuDialog.Callback callback;

    private void setCallback(@NonNull LoginMenuDialog.Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login_menu, container, false);

        TextView loginBtn = view.findViewById(R.id.login);
        loginBtn.setOnClickListener(view1 -> onLoginClick());

        return view;
    }

    @Override
    public void onDestroyView() {
        this.callback = null;
        super.onDestroyView();
    }

    private void onLoginClick() {
        if (LoginMenuDialog.this.callback != null) {
            LoginMenuDialog.this.callback.login();
        }
        dismiss();
    }

    public static class Builder {
        private LoginMenuDialog.Callback callback;

        private Builder() {
        }

        static Builder create() {
            return new Builder();
        }

        Builder setCallback(@NonNull LoginMenuDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            LoginMenuDialog dialog = new LoginMenuDialog();
            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void login();
    }
}
