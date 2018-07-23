package io.viktorot.notefy.repo;

import android.text.TextUtils;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.viktorot.notefy.data.Note;
import timber.log.Timber;

public class NotesRepo {

    private static final String NODE_NOTES = "notes";

    public static abstract class Event {
        @AutoValue
        public abstract static class Added extends Event {

            public abstract Note data();

            static Event.Added create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Added(data);
            }
        }

        @AutoValue
        public abstract static class Changed extends Event {

            public abstract Note data();

            static Event.Changed create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Changed(data);
            }
        }

        @AutoValue
        public abstract static class Removed extends Event {

            public abstract Note data();

            static Event.Removed create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Removed(data);
            }
        }
    }

    private final FirebaseDatabase db;
    private final DatabaseReference ref;

    private final ValueEventListener valueEventListener;
    private final ChildEventListener childEventListener;

    public PublishRelay<NotesRepo.Event> noteChanges = PublishRelay.create();
    public BehaviorRelay<List<Note>> notes = BehaviorRelay.create();

    public NotesRepo(@NonNull FirebaseDatabase db) {
        this.db = db;
        this.ref = db.getReference().child(NODE_NOTES);

        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Note> data = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    data.add(parseSnapshot(child));
                }
                notes.accept(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e(databaseError.toException(), "cancelled");
            }
        };

        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = parseSnapshot(dataSnapshot);

                Timber.d("added => %s", note.getKey());
                noteChanges.accept(Event.Added.create(note));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = parseSnapshot(dataSnapshot);

                Timber.d("changed => %s", note.getKey());
                noteChanges.accept(Event.Changed.create(note));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Note note = parseSnapshot(dataSnapshot);

                Timber.d("removed => %s", note.getKey());
                noteChanges.accept(Event.Removed.create(note));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }

    public void attachListener() {
        //this.ref.addChildEventListener(this.childEventListener);
        this.ref.addValueEventListener(this.valueEventListener);
    }

    public void detachListener() {
        //this.ref.removeEventListener(this.childEventListener);
        this.ref.removeEventListener(this.valueEventListener);
    }

    @NonNull
    private Note parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
        Note note = dataSnapshot.getValue(Note.class);
        Objects.requireNonNull(note);

        note.setKey(dataSnapshot.getKey());

        return note;
    }

    public void save(@NonNull Note note) {
        // TODO: attach Task callbacks
        if (TextUtils.isEmpty(note.getKey())) {
            ref.push().setValue(note);
        } else {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(note.getKey(), note.toMap());
            ref.updateChildren(updates);
        }
    }

    public void delete(@NonNull Note note) {
        if (TextUtils.isEmpty(note.getKey())) {
            Timber.w("cannot delete note without key");
            return;
        }
        ref.child(note.getKey()).removeValue();
    }
}
