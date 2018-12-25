package io.viktorot.notefy.ui.main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.ColorRepo;
import io.viktorot.notefy.repo.TagRepo;

public class FilterDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = FilterDialog.class.getSimpleName();

    private static final String ARG_SELECTED_COLOR = "arg_selected_color";
    private static final String ARG_SELECTED_TAG_ID = "arg_selected_tag_id";

    @Nullable
    private FilterDialog.Callback callback;

    private ColorRepo colorRepo;
    private TagRepo tagRepo;

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorRepo = NotefyApplication.get(requireContext()).getColorRepo();
        tagRepo = NotefyApplication.get(requireContext()).getTagRepo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);

        Bundle args = getArguments();
        final int initialSelectedTagId;
        if (args != null && args.containsKey(ARG_SELECTED_TAG_ID)) {
            initialSelectedTagId = args.getInt(ARG_SELECTED_TAG_ID, -1);
        } else {
            initialSelectedTagId = -1;
        }

        final int initialSelectedColorIndex;
        if (args != null && args.containsKey(ARG_SELECTED_COLOR)) {
            String color = args.getString(ARG_SELECTED_COLOR, "");
            initialSelectedColorIndex = ColorRepo.getColorIndex(color);
        } else {
            initialSelectedColorIndex = -1;
        }

        ChipGroup colorGroup = view.findViewById(R.id.group_color);
        colorGroup.setSingleSelection(true);


        for (int i = 0; i < ColorRepo.COLORS.size(); i++) {
            String colorHash = ColorRepo.getColor(i);
            final Chip chip = (Chip) inflater.inflate(R.layout.item_color_filter, colorGroup, false);
            chip.setId(i);
            chip.setText(colorHash, TextView.BufferType.NORMAL);

            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_chip_icon);
            Objects.requireNonNull(icon);
            icon.mutate();
            DrawableCompat.setTint(icon, Color.parseColor(colorHash));
            chip.setChipIcon(icon);

            chip.setChipBackgroundColor(new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(colorHash),
                            ContextCompat.getColor(requireContext(), R.color.chip)
                    }
            ));

            colorGroup.addView(chip);
        }

        colorGroup.check(initialSelectedColorIndex);
        colorGroup.setOnCheckedChangeListener((chipGroup, index) -> {
            onColorClick(index);
        });

        ChipGroup tagGroup = view.findViewById(R.id.group_tag);
        tagGroup.setSingleSelection(true);

        for (int i = 0; i < tagRepo.getTags().length; i++) {
            int id = i + 1;
            Chip chip = (Chip) inflater.inflate(R.layout.item_tag_filter, colorGroup, false);
            chip.setId(id);
            chip.setText(tagRepo.getTag(id), TextView.BufferType.NORMAL);

            tagGroup.addView(chip);
        }

        tagGroup.check(initialSelectedTagId);
        tagGroup.setOnCheckedChangeListener((chipGroup, id) -> {
            if (id == -1) {
                onTagClick(TagRepo.ID_NONE);
            } else {
                onTagClick(id);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        callback = null;
        super.onDestroyView();
    }

    private void onColorClick(int index) {
        String color = ColorRepo.getColor(index);
        if (callback != null) {
            callback.onColorSelected(color);
        }
    }

    private void onTagClick(int id) {
        if (callback != null) {
            callback.onTagSelected(id);
        }
    }

    public static class Builder {
        private FilterDialog.Callback callback;
        private String selectedColor = "";
        private int selectedTagId = TagRepo.ID_NONE;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setSelectedColor(@NonNull String color) {
            this.selectedColor = color;
            return this;
        }

        public Builder setSelectedTagId(int id) {
            this.selectedTagId = id;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            Bundle args = new Bundle();
            args.putString(ARG_SELECTED_COLOR, this.selectedColor);
            args.putInt(ARG_SELECTED_TAG_ID, this.selectedTagId);

            FilterDialog dialog = new FilterDialog();
            dialog.setCallback(this.callback);
            dialog.setArguments(args);

            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onColorSelected(@NonNull String color);

        void onTagSelected(int id);
    }
}
