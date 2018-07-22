package io.viktorot.notefy.ui.details.tags;

import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.ui.details.icons.IconDialog;

public class TagDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = IconDialog.class.getSimpleName();

    private static final int COLUMN_COUNT = 4;

    @Nullable
    private TagDialog.Callback callback;

    private RecyclerView recycler;

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

        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        adapter.add(getAdapterSection());

        adapter.setOnItemClickListener((item, view1) -> {
//            IconListViewItem iconView = (IconListViewItem) item;
//            onIconClick(iconView.getIconResId());
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
