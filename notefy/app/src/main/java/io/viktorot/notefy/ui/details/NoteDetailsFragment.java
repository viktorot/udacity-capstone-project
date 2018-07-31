package io.viktorot.notefy.ui.details;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxrelay2.PublishRelay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import io.github.mthli.knife.KnifeText;
import io.reactivex.disposables.Disposable;
import io.viktorot.notefy.Navigatable;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.ui.details.colors.ColorDialog;
import io.viktorot.notefy.ui.details.icons.IconDialog;
import io.viktorot.notefy.ui.details.tags.TagDialog;
import io.viktorot.notefy.util.KeyboardUtils;
import io.viktorot.notefy.util.StatusBarUtils;
import io.viktorot.notefy.util.ViewUtils;
import timber.log.Timber;

public class NoteDetailsFragment extends Fragment implements Navigatable {

    public static final String TAG = NoteDetailsFragment.class.getSimpleName();

    private static final String ARG_NOTE = "arg_note";
    private static final String ARG_EDITED = "arg_edited";

    private static final int PIN_ITEM_INDEX = 0;

    public static NoteDetailsFragment newInstance(@NonNull Note note) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);

        NoteDetailsFragment fragment = new NoteDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private PublishRelay<Boolean> keyboardStateRelay = PublishRelay.create();
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private Disposable keyboardStateDisposable;

    private NoteDetailsViewModel vm;

    private TagRepo tagRepo;

    private View root;
    private NestedScrollView scrollView;
    private View holder;

    private Toolbar toolbar;
    private ImageView imgIcon;
    private TextView tvTitle;
    private KnifeText tvContent;
    private TextView tvTag;
    private View editorToolbar;

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

        tagRepo = NotefyApplication.get(requireContext()).getTagRepo();

        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        Note note = null;
        boolean edited = false;

        if (args != null && args.containsKey(ARG_NOTE)) {
            Timber.d("restoring note state");

            note = args.getParcelable(ARG_NOTE);
            edited = args.getBoolean(ARG_EDITED, false);
        }
        if (note == null) {
            throw new IllegalArgumentException("note cannot be null");
        }

        vm = ViewModelProviders.of(this).get(NoteDetailsViewModel.class);
        vm.init(note, edited);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);

        final int h = getResources().getDimensionPixelSize(R.dimen.editor_toolbar_height);

        globalLayoutListener = () -> {
            Rect measureRect = new Rect();
            view.getWindowVisibleDisplayFrame(measureRect);

            // measureRect.bottom is the position above soft keypad
            int keypadHeight = view.getRootView().getHeight() - measureRect.bottom;

            if (keypadHeight > h) {
                keyboardStateRelay.accept(true);
            } else {
                keyboardStateRelay.accept(false);
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        scrollView = view.findViewById(R.id.scroll_view);
        holder = view.findViewById(R.id.holder);

        keyboardStateDisposable = keyboardStateRelay
                .distinctUntilChanged()
                .subscribe(visible -> {
                    Timber.v("keyboard visible => %b", visible);
                    if (!visible) {
                        tvTitle.clearFocus();
                        tvContent.clearFocus();
                    }
                });

        vm.action.observe(getViewLifecycleOwner(), actionObserver);
        vm.data.observe(getViewLifecycleOwner(), dataObserver);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(view1 -> {
            vm.close();
        });

        toolbar.inflateMenu(R.menu.details);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        pinMenuItem = toolbar.getMenu().getItem(PIN_ITEM_INDEX);

        imgIcon = view.findViewById(R.id.icon);
        imgIcon.setColorFilter(Color.WHITE);
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
                onTitleChanged();
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
                onContentChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

//        scrollView.setOnClickListener(view16 -> {
//            Timber.v("scroll click");
//        });

        holder.setOnClickListener(view15 -> {
            KeyboardUtils.hideKeyboard(requireActivity());
        });

        tvTitle.setOnFocusChangeListener((view14, hasFocus) -> {
            if (!hasFocus) {
                KeyboardUtils.hideKeyboard(requireActivity(), view14);
            }
        });

        tvContent.setOnFocusChangeListener((view12, hasFocus) -> {
            if (!hasFocus) {
                KeyboardUtils.hideKeyboard(requireActivity(), view12);
            }
        });

        tvTag = view.findViewById(R.id.tag);
        editorToolbar = view.findViewById(R.id.editor_toolbar);

        ImageButton btnBold = view.findViewById(R.id.bold);
        btnBold.setOnClickListener(view1 -> {
            tvContent.bold(!tvContent.contains(KnifeText.FORMAT_BOLD));
            onContentChanged();
        });

        ImageButton btnItalic = view.findViewById(R.id.italic);
        btnItalic.setOnClickListener(view1 -> {
            tvContent.italic(!tvContent.contains(KnifeText.FORMAT_ITALIC));
            onContentChanged();
        });

        ImageButton btnUnderline = view.findViewById(R.id.underline);
        btnUnderline.setOnClickListener(view1 -> {
            tvContent.underline(!tvContent.contains(KnifeText.FORMAT_UNDERLINED));
            onContentChanged();
        });

        ImageButton btnStrikethrugh = view.findViewById(R.id.strkethrough);
        btnStrikethrugh.setOnClickListener(view1 -> {
            tvContent.strikethrough(!tvContent.contains(KnifeText.FORMAT_STRIKETHROUGH));
            onContentChanged();
        });

        ImageButton btnBulletList = view.findViewById(R.id.bullet);
        btnBulletList.setOnClickListener(view1 -> {
            tvContent.bullet(!tvContent.contains(KnifeText.FORMAT_BULLET));
            onContentChanged();
        });

        return view;
    }

    private void onTitleChanged() {
        vm.onTitleChanged(tvTitle.getText().toString());
    }

    private void onContentChanged() {
        vm.onContentChanged(tvContent.toHtml());
    }

    @Override
    public void onDestroyView() {
        if (keyboardStateDisposable != null) {
            keyboardStateDisposable.dispose();
        }

        View view = getView();
        if (globalLayoutListener != null && view != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }

        StatusBarUtils.setColor(requireActivity(),
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));

        hideProgressDialog();

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
        } else if (menuItem.getItemId() == R.id.action_delete) {
            vm.deleteNote();
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
        } else if (action == NoteDetailsViewModel.Action.ShowDeleteConfirmation) {
            showDeleteConfirmationDialog();
        } else if (action == NoteDetailsViewModel.Action.ShowProgress) {
            showProgressDialog();
        } else if (action == NoteDetailsViewModel.Action.HideProgress) {
            hideProgressDialog();
        } else if (action == NoteDetailsViewModel.Action.ShowEmptyTitleError) {
            Toast.makeText(requireContext(), "[title must be set before saving note]", Toast.LENGTH_LONG).show();
        }
    }

    private void onDataChanged(@NonNull Note note) {
        tvTitle.setText(note.getTitle());
        tvContent.fromHtml(note.getContent());

        if (tagRepo.isIdValid(note.getTagId())) {
            tvTag.setText(tagRepo.getTag(note.getTagId()));
            ViewUtils.show(tvTag);
        } else {
            ViewUtils.hide(tvTag);
        }

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
                .setSelectedItem(vm.getTagId())
                .show(requireFragmentManager());
    }

    private void showDeleteConfirmationDialog() {
        new MaterialDialog.Builder(requireContext())
                .title(R.string.dialog_delete_title)
                .content(R.string.dialog_delete_content)
                .positiveText(R.string.dialog_delete_confirm)
                .negativeText(R.string.dialog_no)
                .onPositive((dialog, which) -> {
                    vm.delete();
                })
                .show();
    }

    private void showSaveConfirmationDialog() {
        new MaterialDialog.Builder(requireContext())
                .title(R.string.dialog_save_title)
                .content(R.string.dialog_save_content)
                .positiveText(R.string.dialog_save_confirm)
                .negativeText(R.string.dialog_no)
                .onPositive((dialog, which) -> {
                    vm.save();
                })
                .onNegative((dialog, which) -> {
                    vm.pop();
                })
                .show();
    }

    private MaterialDialog progressDialog;

    private void showProgressDialog() {
        hideProgressDialog();

        progressDialog = new MaterialDialog.Builder(requireContext())
                .title(R.string.dialog_saving_title)
                .progress(true, 0)
                .show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Note note = vm.data();
        if (note != null) {
            Timber.d("saving note state");
            outState.putParcelable(ARG_NOTE, note);
            outState.putBoolean(ARG_EDITED, vm.isEdited());
        }
    }

    @Override
    public boolean onBackPressed() {
        if (vm.isEdited()) {
            showSaveConfirmationDialog();
            return false;
        }
        return true;
    }
}
