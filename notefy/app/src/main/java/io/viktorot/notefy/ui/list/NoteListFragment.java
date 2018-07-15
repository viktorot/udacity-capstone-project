package io.viktorot.notefy.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.viktorot.notefy.R;
import io.viktorot.notefy.data.Note;

public class NoteListFragment extends Fragment {

    public static final String TAG = NoteListFragment.class.getSimpleName();

    public static NoteListFragment newInstance() {
        Bundle args = new Bundle();

        NoteListFragment fragment = new NoteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NoteListViewModel vm;

    private RecyclerView recycler;

    private final GroupAdapter adapter = new GroupAdapter();
    private final Section section = new Section();

    private final Observer<List<Note>> notesObserver = notes -> {
        if (notes == null) {
            return;
        }
        onNoteListChanged(notes);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this).get(NoteListViewModel.class);

        adapter.add(section);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        recycler = view.findViewById(R.id.notes_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        vm.notes.observe(getViewLifecycleOwner(), notesObserver);

        return view;
    }

    private void onNoteListChanged(@NonNull List<Note> notes) {
        ArrayList<NoteListViewItem> viewItems = new ArrayList<>();
        for (Note note : notes) {
            viewItems.add(new NoteListViewItem(note));
        }
        section.update(viewItems);
    }
}
