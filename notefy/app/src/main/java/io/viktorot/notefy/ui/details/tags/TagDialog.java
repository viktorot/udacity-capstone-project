package io.viktorot.notefy.ui.details.tags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.ui.details.icons.IconDialog;
import io.viktorot.notefy.ui.view.Chip;
import io.viktorot.notefy.ui.view.ChipGroup;

public class TagDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = IconDialog.class.getSimpleName();

    private static final String ARG_SELECTED_ID = "arg_selected_id";

    @Nullable
    private TagDialog.Callback callback;

    private void setCallback(@NonNull TagDialog.Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_list, container, false);

        Bundle args = getArguments();
        final int initialSelected;
        if (args != null && args.containsKey(ARG_SELECTED_ID)) {
            initialSelected = args.getInt(ARG_SELECTED_ID);
        } else {
            initialSelected = TagRepo.ID_NONE;
        }

        final ChipGroup group = view.findViewById(R.id.group);
        group.setSingleSelection(true);

        group.setOnCheckedChangeListener((chipGroup, i) -> {
            if (i == initialSelected) {
                return;
            }
            if (i == -1) {
                onTagClick(TagRepo.ID_NONE);
            } else {
                onTagClick(i);
            }
        });

        TagRepo repo = NotefyApplication.get(view.getContext()).getTagRepo();

        for (int i = 0; i < repo.getTags().length; i++) {
            int id = i + 1;
            Chip chip = (Chip) inflater.inflate(R.layout.item_tag_list, group, false);
            chip.setId(id);
            chip.setText(repo.getTag(id), TextView.BufferType.NORMAL);
            group.addView(chip);
        }

        if (initialSelected != TagRepo.ID_NONE) {
            group.check(initialSelected);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        callback = null;
        super.onDestroyView();
    }

    private void onTagClick(int tagId) {
        if (callback != null) {
            callback.onTagSelected(tagId);
        }
        dismiss();
    }

    public static class Builder {
        private int selected = TagRepo.ID_NONE;
        private TagDialog.Callback callback;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setSelectedItem(int id) {
            this.selected = id;
            return this;
        }

        public Builder setCallback(@NonNull TagDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            Bundle args = new Bundle();
            args.putInt(ARG_SELECTED_ID, this.selected);

            TagDialog dialog = new TagDialog();
            dialog.setArguments(args);

            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onTagSelected(int tagId);
    }
}
