package io.viktorot.notefy.ui.details.tags;

import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.TagRepo;
import io.viktorot.notefy.ui.details.icons.IconDialog;
import timber.log.Timber;

public class TagDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = IconDialog.class.getSimpleName();

    private static final int COLUMN_COUNT = 4;

    @Nullable
    private TagDialog.Callback callback;

    //private RecyclerView recycler;
    private ChipGroup group;

    private final GroupAdapter adapter = new GroupAdapter();

    private void setCallback(@NonNull TagDialog.Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_list, container, false);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), COLUMN_COUNT);
        layoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());

//        recycler = view.findViewById(R.id.recycler);
//        recycler.setLayoutManager(layoutManager);
//        recycler.setAdapter(adapter);
//
//        adapter.add(getAdapterSection());
//
//        adapter.setOnItemClickListener((item, view1) -> {
//            IconListViewItem iconView = (IconListViewItem) item;
//            onIconClick(iconView.getIconResId());
//        });

        group = view.findViewById(R.id.group);
        group.setSingleSelection(true);

        group.setOnCheckedChangeListener((chipGroup, i) -> {
            Timber.v("checked => %d", i);
        });

        TagRepo repo = NotefyApplication.get(view.getContext()).getTagRepo();

        for (int i = 0; i < repo.getTags().length; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_tag_list, group, false);
            chip.setId(i);
            chip.setText(repo.getTag(i), TextView.BufferType.NORMAL);
            group.addView(chip);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        // TODO: clean callbacks
        super.onDestroyView();
    }

    private void onTagClick() {

    }

    @NonNull
    private Section getAdapterSection() {
        ArrayList<TagListViewItem> items = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            items.add(new TagListViewItem(String.valueOf(i)));
        }

        return new Section(items);
    }

    public static class Builder {
        private TagDialog.Callback callback;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setCallback(@NonNull TagDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            TagDialog dialog = new TagDialog();
            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onTagSelected();
    }
}
