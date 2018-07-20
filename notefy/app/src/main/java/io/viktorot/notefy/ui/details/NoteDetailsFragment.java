package io.viktorot.notefy.ui.details;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.ui.details.colors.ColorDialog;
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

    private final Observer<Note> dataObserver = data -> {
        if (data == null) {
            return;
        }
        onDataChanged(data);
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
        vm.data.observe(getViewLifecycleOwner(), dataObserver);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view1 -> {
            vm.back();
        });

        imgIcon = view.findViewById(R.id.icon);
        imgIcon.setOnClickListener(view1 -> {
            //vm.selectIcon();
            vm.selectColor();
        });

        return view;
    }

    private void onViewModelAction(NoteDetailsViewModel.Action action) {
        if (action == NoteDetailsViewModel.Action.SelectIcon) {
            openIconMenu();
        } else if (action == NoteDetailsViewModel.Action.SelectColor) {
            openColorMenu();
        }
    }

    private void onDataChanged(@NonNull Note note) {
        int iconResId = NotefyApplication.get(requireContext())
                .getIconsRepo().getIconRes(note.getIconId());
        imgIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconResId));

        toolbar.setBackgroundColor(Color.parseColor(note.getColor()));
    }

    private void openIconMenu() {
        IconDialog.Builder.create()
                .setCallback(vm::onIconSelected)
                .show(getFragmentManager());
    }

    private void openColorMenu() {
        ColorDialog.Builder.create()
                .setCallback(vm::onColorSelected)
                .show(getFragmentManager());
    }
}
