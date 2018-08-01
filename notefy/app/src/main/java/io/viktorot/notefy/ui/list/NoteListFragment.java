package io.viktorot.notefy.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.Section;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;
import io.viktorot.notefy.util.ViewUtils;
import timber.log.Timber;

public class NoteListFragment extends Fragment {

    public static final String TAG = NoteListFragment.class.getSimpleName();

    public static NoteListFragment newInstance() {
        Bundle args = new Bundle();

        NoteListFragment fragment = new NoteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NoteListViewModel vm;

    private TextView tvEmpty;
    private ContentLoadingProgressBar progress;
    private RecyclerView recycler;

    private final GroupAdapter adapter = new GroupAdapter();
    private final Section section = new Section();

    private final Observer<List<Note>> notesObserver = notes -> {
        if (notes == null) {
            return;
        }
        onNoteListChanged(notes);
    };

    private final Observer<NoteListViewModel.State> stateObserver = state -> {
        if (state == null) {
            return;
        }
        onStateChanged(state);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this).get(NoteListViewModel.class);

        adapter.add(section);
        adapter.setOnItemClickListener((item, view) -> {
            NoteListViewItem viewItem = (NoteListViewItem) item;
            vm.editNote(viewItem.getData());
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        tvEmpty = view.findViewById(R.id.empty_label);
        progress = view.findViewById(R.id.progress);

        recycler = view.findViewById(R.id.notes_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        vm.state.observe(getViewLifecycleOwner(), stateObserver);
        vm.notes.observe(getViewLifecycleOwner(), notesObserver);

        return view;
    }

    private void onStateChanged(NoteListViewModel.State state) {
        if (state == NoteListViewModel.State.Loading) {
            ViewUtils.hide(tvEmpty);
            ViewUtils.hide(recycler);
            ViewUtils.show(progress);
        } else if (state == NoteListViewModel.State.Empty) {
            ViewUtils.hide(recycler);
            ViewUtils.hide(progress);
            ViewUtils.show(tvEmpty);
        } else if (state == NoteListViewModel.State.Data) {
            ViewUtils.hide(tvEmpty);
            ViewUtils.hide(progress);
            ViewUtils.show(recycler);
        }
    }

    private void onNoteListChanged(@NonNull List<Note> notes) {
        Timber.d("note list changed");
        ArrayList<NoteListViewItem> viewItems = new ArrayList<>();
        for (Note note : notes) {
            viewItems.add(new NoteListViewItem(requireContext(), note));
        }
        section.update(viewItems);
    }
}
