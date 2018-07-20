package io.viktorot.notefy.ui.details.colors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.viktorot.notefy.NotefyBottomSheetDialogFragment;
import io.viktorot.notefy.R;
import io.viktorot.notefy.repo.ColorRepo;

public class ColorDialog extends NotefyBottomSheetDialogFragment {

    private static final String TAG = ColorDialog.class.getSimpleName();

    private static final int COLUMN_COUNT = 4;

    @Nullable
    private ColorDialog.Callback callback;

    private RecyclerView recycler;

    private final GroupAdapter adapter = new GroupAdapter();

    private void setCallback(@NonNull ColorDialog.Callback callback) {
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
            ColorListViewItem colorView = (ColorListViewItem) item;
            onColorClick(colorView.getColor());
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        callback = null;
        super.onDestroyView();
    }

    private void onColorClick(String color) {
        if (callback != null) {
            callback.onColorSelected(color);
        }
        dismiss();
    }

    @NonNull
    private Section getAdapterSection() {
        ArrayList<ColorListViewItem> items = new ArrayList<>();

        for (String color : ColorRepo.COLORS) {
            items.add(new ColorListViewItem(color));
        }

        return new Section(items);
    }

    public static class Builder {
        private ColorDialog.Callback callback;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setCallback(@NonNull ColorDialog.Callback callback) {
            this.callback = callback;
            return this;
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            ColorDialog dialog = new ColorDialog();
            dialog.setCallback(this.callback);
            dialog.show(fragmentManager, TAG);
        }
    }

    public interface Callback {
        void onColorSelected(String color);
    }
}
