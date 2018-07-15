package io.viktorot.notefy.repo;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxrelay2.PublishRelay;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.viktorot.notefy.data.Note;
import timber.log.Timber;

public class NotesRepo {

    private static final String NODE_NOTES = "notes";

    private final FirebaseDatabase db;
    private final DatabaseReference ref;
    private final ChildEventListener listener;

    public PublishRelay<Note> notes = PublishRelay.create();

    public NotesRepo(@NonNull FirebaseDatabase db) {
        this.db = db;
        this.ref = db.getReference().child(NODE_NOTES);

        this.listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = dataSnapshot.getValue(Note.class);
                Objects.requireNonNull(note);

                Timber.d("added => %s", note.getTitle());
                notes.accept(note);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        this.ref.addChildEventListener(this.listener);
    }

    public void save(@NonNull Note note) {
        ref.push().setValue(note);
    }
}
