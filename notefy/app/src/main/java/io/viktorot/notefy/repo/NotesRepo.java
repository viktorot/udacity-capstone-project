package io.viktorot.notefy.repo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import io.viktorot.notefy.data.Note;

public class NotesRepo {

    private static final String NOTES = "notes";

    private final FirebaseDatabase db;
    private final DatabaseReference notesDbReference;

    public NotesRepo(@NonNull FirebaseDatabase db) {
        this.db = db;
        this.notesDbReference = db.getReference().child(NOTES);
    }

    public void save(@NonNull Note note) {
        notesDbReference.push().setValue(note);
    }
}
