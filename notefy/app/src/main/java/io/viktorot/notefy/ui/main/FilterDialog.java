package io.viktorot.notefy.ui.main;

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

        // TODO: set selected color
        ChipGroup colorGroup = view.findViewById(R.id.group_color);
        colorGroup.setSingleSelection(true);

        colorGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            onColorClick(i);
        });

        for (int i = 0; i < ColorRepo.COLORS.size(); i++) {
            String colorHash = ColorRepo.getColor(i);
            Chip chip = (Chip) inflater.inflate(R.layout.item_color_filter, colorGroup, false);
            chip.setId(i);
            chip.setText(colorHash, TextView.BufferType.NORMAL);

            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_chip_icon);
            Objects.requireNonNull(icon);
            icon.mutate();
            DrawableCompat.setTint(icon, Color.parseColor(colorHash));
            chip.setChipIcon(icon);

            colorGroup.addView(chip);
        }


        // TODO: set selected color
        ChipGroup tagGroup = view.findViewById(R.id.group_tag);
        tagGroup.setSingleSelection(true);

        for (int i = 0; i < tagRepo.getTags().length; i++) {
            int id = i + 1;
            Chip chip = (Chip) inflater.inflate(R.layout.item_tag_filter, colorGroup, false);
            chip.setId(id);
            chip.setText(tagRepo.getTag(id), TextView.BufferType.NORMAL);

            tagGroup.addView(chip);
        }


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
        dismiss();
    }

    public static class Builder {
        private FilterDialog.Callback callback;


        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            FilterDialog dialog = new FilterDialog();
            dialog.setCallback(this.callback);

            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onColorSelected(@NonNull String color);
    }
}
