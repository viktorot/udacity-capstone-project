package io.viktorot.notefy.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.ColorRepo;

public class FilterDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = FilterDialog.class.getSimpleName();

    private ColorRepo colorRepo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorRepo = NotefyApplication.get(requireContext()).getColorRepo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);

        ChipGroup colorGroup = view.findViewById(R.id.group_color);
//        colorGroup.setSingleLine(true);

        for (int i = 0; i < ColorRepo.COLORS.size(); i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_tag_list, colorGroup, false);
            chip.setId(i);
            chip.setText(ColorRepo.COLORS.get(i), TextView.BufferType.NORMAL);
            colorGroup.addView(chip);
        }

        return view;
    }

    public static class Builder {

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            FilterDialog dialog = new FilterDialog();
            dialog.show(fragmentManager, TAG);
        }
    }
}
