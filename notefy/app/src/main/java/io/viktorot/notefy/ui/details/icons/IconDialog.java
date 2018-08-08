package io.viktorot.notefy.ui.details.icons;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;

public class IconDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = IconDialog.class.getSimpleName();

    private static final int COLUMN_COUNT = 4;

    private final List<Integer> ICON_IDS = Arrays.asList(
            R.drawable.ic_android,
            R.drawable.ic_attach,
            R.drawable.ic_bookmark,
            R.drawable.ic_check,
            R.drawable.ic_doc,
            R.drawable.ic_dot,
            R.drawable.ic_extension,
            R.drawable.ic_face,
            R.drawable.ic_folder,
            R.drawable.ic_run
    );

    @Nullable
    private IconDialog.Callback callback;

    private RecyclerView recycler;

    private final GroupAdapter adapter = new GroupAdapter();

    private void setCallback(@NonNull IconDialog.Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setSpanCount(COLUMN_COUNT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_icon_list, container, false);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), COLUMN_COUNT);
        layoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());

        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        adapter.add(getAdapterSection());

        adapter.setOnItemClickListener((item, view1) -> {
            IconListViewItem iconView = (IconListViewItem) item;
            onIconClick(iconView.getIconResId());
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        this.callback = null;
        super.onDestroyView();
    }

    private void onIconClick(@DrawableRes int iconResId) {
        if (callback != null) {
            callback.onIconSelected(iconResId);
        }
        dismiss();
    }

    @NonNull
    private Section getAdapterSection() {
        ArrayList<IconListViewItem> items = new ArrayList<>();

        for (int id : ICON_IDS) {
            Drawable drawable = ContextCompat.getDrawable(requireContext(), id);
            Objects.requireNonNull(drawable);
            items.add(new IconListViewItem(id, drawable));
        }

        return new Section(items);
    }

    public static class Builder {
        private IconDialog.Callback callback;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setCallback(@NonNull IconDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            IconDialog dialog = new IconDialog();
            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onIconSelected(@DrawableRes int iconId);
    }
}
