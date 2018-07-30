package io.viktorot.notefy.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.AuthRepo;

public class AppMenuDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = AppMenuDialog.class.getSimpleName();

    private AuthRepo authRepo;

    @Nullable
    private AppMenuDialog.Callback callback;

    private void setCallback(@NonNull AppMenuDialog.Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authRepo = NotefyApplication.get(requireContext()).getAuthRepo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_app_menu, container, false);

//        ImageView img = view.findViewById(R.id.profile);
//        img.setImageResource();

        TextView btnLogout = view.findViewById(R.id.logout);
        btnLogout.setOnClickListener(view1 -> onLogoutClick());

        TextView tvName = view.findViewById(R.id.name);
        tvName.setText(authRepo.getUserEmail());

        return view;
    }

    @Override
    public void onDestroyView() {
        this.callback = null;
        super.onDestroyView();
    }

    private void onLogoutClick() {
        if (callback != null) {
            callback.logout();
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
