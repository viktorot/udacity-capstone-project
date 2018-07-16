package io.viktorot.notefy.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.R;

public class AppMenuDialog extends BottomSheetDialogFragment {

    private static final String TAG = AppMenuDialog.class.getSimpleName();

    @Nullable
    private AppMenuDialog.Callback callback;

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    private void setCallback(@NonNull AppMenuDialog.Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_app_menu, container, false);

        Button btnLogout = view.findViewById(R.id.logout);
        btnLogout.setOnClickListener(view1 -> onLogoutClick());

        return view;
    }

    @Override
    public void onDestroyView() {
        this.callback = null;
        super.onDestroyView();
    }

    private void onLogoutClick() {
        if (AppMenuDialog.this.callback != null) {
            AppMenuDialog.this.callback.logout();
        }
        dismiss();
    }

    public static class Builder {
        private AppMenuDialog.Callback callback;

        private Builder() {
        }

        static Builder create() {
            return new Builder();
        }

        Builder setCallback(@NonNull AppMenuDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            AppMenuDialog dialog = new AppMenuDialog();
            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void logout();
    }

}