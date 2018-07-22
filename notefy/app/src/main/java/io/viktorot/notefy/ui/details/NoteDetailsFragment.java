package io.viktorot.notefy.ui.details;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.viktorot.notefy.Navigatable;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.ui.details.colors.ColorDialog;
import io.viktorot.notefy.ui.details.icons.IconDialog;
import io.viktorot.notefy.ui.details.tags.TagDialog;
import io.viktorot.notefy.util.StatusBarUtils;

public class NoteDetailsFragment extends Fragment implements Navigatable {

    public static final String TAG = NoteDetailsFragment.class.getSimpleName();

    private static final String ARG_NOTE = "arg_note";

    private static final int PIN_ITEM_INDEX = 0;

    public static NoteDetailsFragment newInstance(@NonNull Note note) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);

        NoteDetailsFragment fragment = new NoteDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NoteDetailsViewModel vm;

    private Toolbar toolbar;
    private ImageView imgIcon;
    private TextView tvTitle;
    private TextView tvContent;

    private MenuItem pinMenuItem;

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

        Bundle args = getArguments();
        Note note = null;
        if (args != null && args.containsKey(ARG_NOTE)) {
            note = args.getParcelable(ARG_NOTE);
        }
        if (note == null) {
            throw new IllegalArgumentException("note cannot be null");
        }

        vm = ViewModelProviders.of(this).get(NoteDetailsViewModel.class);
        vm.init(note);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);

        vm.action.observe(getViewLifecycleOwner(), actionObserver);
        vm.data.observe(getViewLifecycleOwner(), dataObserver);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(view1 -> {
            vm.back();
        });

        toolbar.inflateMenu(R.menu.details);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        pinMenuItem = toolbar.getMenu().getItem(PIN_ITEM_INDEX);

        imgIcon = view.findViewById(R.id.icon);
        imgIcon.setOnClickListener(view1 -> {
            vm.selectIcon();
        });

        tvTitle = view.findViewById(R.id.title);
        tvTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                vm.onTitleChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvContent = view.findViewById(R.id.content);
        tvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                vm.onContentChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        // TODO: do this in onBackPressed
        StatusBarUtils.setColor(requireActivity(), ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        super.onDestroyView();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_color) {
            vm.selectColor();
            return true;
        } else if (menuItem.getItemId() == R.id.action_tag) {
            vm.selectTag();
            return true;
        } else if (menuItem.getItemId() == R.id.action_pin) {
            vm.togglePinnedState();
            return true;
        }
        return false;
    }

    private void onViewModelAction(NoteDetailsViewModel.Action action) {
        if (action == NoteDetailsViewModel.Action.SelectIcon) {
            openIconMenu();
        } else if (action == NoteDetailsViewModel.Action.SelectColor) {
            openColorMenu();
        } else if (action == NoteDetailsViewModel.Action.SelectTag) {
            openTagMenu();
        }
    }

    private void onDataChanged(@NonNull Note note) {
        tvTitle.setText(note.getTitle());
        tvContent.setText(note.getContent());

        int iconResId = NotefyApplication.get(requireContext())
                .getIconRepo().getIconRes(note.getIconId());
        imgIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconResId));

        int color = Color.parseColor(note.getColor());
        toolbar.setBackgroundColor(color);

        StatusBarUtils.setColor(requireActivity(), color);

        // TODO: cache drawables
        Drawable drawable;
        if (note.isPinned()) {
            drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pined);
        } else {
            drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pin);
        }
        pinMenuItem.setIcon(drawable);
    }

    private void openIconMenu() {
        IconDialog.Builder.create()
                .setCallback(vm::onIconSelected)
                .show(requireFragmentManager());
    }

    private void openColorMenu() {
        ColorDialog.Builder.create()
                .setCallback(vm::onColorSelected)
                .show(requireFragmentManager());
    }

    private void openTagMenu() {
        TagDialog.Builder.create()
                .setCallback(vm::onTagSelected)
                .setSelectedItem(2)
                .show(requireFragmentManager());
    }

    @Override
    public boolean onBackPressed() {
        if (vm.isEdited()) {
            new MaterialDialog.Builder(requireContext())
                    .title("[save]")
                    .content("[do you want to save changes?]")
                    .positiveText("[yes]")
                    .negativeText("[no]")
                    .onPositive((dialog, which) -> {
                        vm.save();
                    })
                    .onNegative((dialog, which) -> {
                        vm.pop();
                    })
                    .show();

            return false;
        }
        return true;
    }
}
