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
    private View holder;


    private Toolbar toolbar;
    private ImageView imgIcon;
    private TextView tvTitle;
    private KnifeText tvContent;
    //    private TextView tvTag;
    private View editorToolbar;
    private ImageButton btnBold;
    private ImageButton btnItalic;
    private ImageButton btnUnderline;
    private ImageButton btnBulletList;

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

    int prev = -1;
    int prevToolbar = -1;

    int h = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);

        h = getResources().getDimensionPixelSize(R.dimen.editor_toolbar_height);

        globalLayoutListener = () -> {
            Rect measureRect = new Rect(); //you should cache this, onGlobalLayout can get called often
            view.getWindowVisibleDisplayFrame(measureRect);

            // measureRect.bottom is the position above soft keypad
            int keypadHeight = view.getRootView().getHeight() - measureRect.bottom;

            Timber.v("keyboard height => %d", keypadHeight);

            if (keypadHeight > 150) {
                keyboardStateRelay.accept(true);
            } else {
                keyboardStateRelay.accept(false);
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        root = view.findViewById(R.id.root);
        root.setNestedScrollingEnabled(true);
        root.setOnClickListener(view13 -> {
            tvContent.requestFocus();
        });

        holder = view.findViewById(R.id.holder);

        keyboardStateDisposable = keyboardStateRelay
                .distinctUntilChanged()
                .subscribe(visible -> {
                    Timber.v("keyboard visible => %b", visible);
                    if (visible) {
//                        prev = holder.getBottom();
//                        holder.setBottom(934);

//                        prevToolbar = editorToolbar.getBottom();
//                        editorToolbar.setBottom(790);

//                        ViewUtils.show(editorToolbar);

//                        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) root.getLayoutParams());
//                        params.setMargins(0, 0, 0, 718);
//                        root.setLayoutParams(params);
//                        root.setBottom(718);
                    } else {
//                        ViewUtils.hide(editorToolbar);

                        if (prev > -1) {
                            holder.setBottom(prev);
                            prev = -1;
                        }

                        if (prevToolbar > -1) {
                            editorToolbar.setBottom(prevToolbar);
                            prevToolbar = -1;
                        }
                    }
//                    ((FrameLayout.LayoutParams) root.getLayoutParams())
//                            .setMargins(0, 0, 0, 0);
                });

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
                vm.onContentChanged(tvContent.toHtml());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tvContent.setOnFocusChangeListener((view12, hasFocus) -> {
            if (hasFocus) {
//                ViewUtils.hide(tvTag);
//                ViewUtils.show(editorToolbar);
            } else {
//                ViewUtils.hide(editorToolbar);
                // TODO: show only if tag available
//                ViewUtils.show(tvTag);

                KeyboardUtils.hideKeyboard(requireActivity(), tvContent);
            }
        });

//        tvTag = view.findViewById(R.id.tag);
        editorToolbar = view.findViewById(R.id.editor_toolbar);

//        ViewUtils.hide(editorToolbar);
//        ViewUtils.show(tvTag);

        btnBold = view.findViewById(R.id.bold);
        btnBold.setOnClickListener(view1 -> {
            tvContent.bold(!tvContent.contains(KnifeText.FORMAT_BOLD));
            vm.edited();
        });

        btnItalic = view.findViewById(R.id.italic);
        btnItalic.setOnClickListener(view1 -> {
            tvContent.italic(!tvContent.contains(KnifeText.FORMAT_ITALIC));
            vm.edited();
        });

        btnUnderline = view.findViewById(R.id.underline);
        btnUnderline.setOnClickListener(view1 -> {
            tvContent.underline(!tvContent.contains(KnifeText.FORMAT_UNDERLINED));
            vm.edited();
        });

        btnBulletList = view.findViewById(R.id.bullet);
        btnBulletList.setOnClickListener(view1 -> {
            tvContent.bullet(!tvContent.contains(KnifeText.FORMAT_BULLET));
            vm.edited();
        });

        return view;
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
        }
    }

    private void onDataChanged(@NonNull Note note) {
        tvTitle.setText(note.getTitle());
        tvContent.fromHtml(note.getContent());

//        if (tagRepo.isIdValid(note.getTagId())) {
//            tvTag.setText(tagRepo.getTag(note.getTagId()));
//            ViewUtils.show(tvTag);
//        } else {
//            ViewUtils.hide(tvTag);
//        }

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
                .title("[delete]")
                .content("[are you sure you want to delete this note?]")
                .positiveText("[yes]")
                .negativeText("[no]")
                .onPositive((dialog, which) -> {
                    vm.delete();
                })
                .show();
    }

    private void showSaveConfirmationDialog() {
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
    }

    @Override
    public boolean onBackPressed() {
        if (vm.isEdited()) {
            showSaveConfirmationDialog();
            return false;
        }

        StatusBarUtils.setColor(requireActivity(),
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        return true;
    }
}
