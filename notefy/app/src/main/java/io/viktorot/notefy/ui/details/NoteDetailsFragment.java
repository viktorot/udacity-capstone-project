package io.viktorot.notefy.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.viktorot.notefy.R;
import io.viktorot.notefy.ui.details.icons.IconDialog;

public class NoteDetailsFragment extends Fragment {

    public static final String TAG = NoteDetailsFragment.class.getSimpleName();

    public static NoteDetailsFragment newInstance() {
        Bundle args = new Bundle();

        NoteDetailsFragment fragment = new NoteDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NoteDetailsViewModel vm;

    private Toolbar toolbar;
    private ImageView imgIcon;

    private final Observer<NoteDetailsViewModel.Action> actionObserver = action -> {
        if (action == null) {
            return;
        }
        onViewModelAction(action);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this).get(NoteDetailsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);

        vm.action.observe(getViewLifecycleOwner(), actionObserver);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view1 -> {
            vm.back();
        });

        imgIcon = view.findViewById(R.id.icon);
        imgIcon.setOnClickListener(view1 -> {
            vm.selectIcon();
        });

        return view;
    }

    private void onViewModelAction(NoteDetailsViewModel.Action action) {
        if (action == NoteDetailsViewModel.Action.SelectIcon) {
            openIconMenu();
        }
    }

    private void openIconMenu() {
        IconDialog.Builder.create()
                .setCallback(vm::onIconSelected)
                .show(getFragmentManager());
    }
}
