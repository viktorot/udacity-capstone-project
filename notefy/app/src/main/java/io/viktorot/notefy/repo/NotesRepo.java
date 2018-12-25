package io.viktorot.notefy.repo;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.viktorot.notefy.NotefyApplication;
import io.viktorot.notefy.data.Note;
import timber.log.Timber;

public class NotesRepo {

    private static final String NODE_NOTES = "notes";

    public static abstract class Event {

        public abstract Note data();

        @AutoValue
        public abstract static class Added extends Event {
            static Event.Added create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Added(data);
            }
        }

        @AutoValue
        public abstract static class Changed extends Event {
            static Event.Changed create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Changed(data);
            }
        }

        @AutoValue
        public abstract static class Removed extends Event {
            static Event.Removed create(@NonNull Note data) {
                return new AutoValue_NotesRepo_Event_Removed(data);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Event event = (Event) o;
            return event.data().equals(this.data());
        }
    }

    private final Context context;

    private final FirebaseDatabase db;
    private final DatabaseReference ref;
    private final DatabaseReference connectedRef;

    private final ValueEventListener valueEventListener;
    private final ValueEventListener connectedEventListener;
    private final ChildEventListener childEventListener;

    private AtomicBoolean attached = new AtomicBoolean(false);

    private PublishRelay<NotesRepo.Event> noteChanges = PublishRelay.create();
    public BehaviorRelay<List<Note>> notes = BehaviorRelay.create();

    public NotesRepo(@NonNull Context context, @NonNull FirebaseDatabase db) {
        this.context = context;

        this.db = db;
        this.db.setPersistenceEnabled(true);

        this.ref = db.getReference().child(NODE_NOTES);
        this.ref.keepSynced(true);

        this.connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        this.connectedEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Timber.d("connected");
                } else {
                    Timber.d("not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Timber.e("connection listener was cancelled");
            }
        };

        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Note> data = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    data.add(parseSnapshot(child));
                }

                notes.accept(data);
                NotefyApplication.get(context).updateWidgets();
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

    public Observable<NotesRepo.Event> getNoteChangesObservable() {
        return this.noteChanges; //.distinctUntilChanged();
    }

    public void clearCache() {
        notes.accept(Collections.emptyList());
        NotefyApplication.get(context).updateWidgets();
    }

    public void attachListener() {
        if (attached.get()) {
            return;
        }

        this.ref.addChildEventListener(this.childEventListener);
        this.ref.addValueEventListener(this.valueEventListener);
        //this.connectedRef.addValueEventListener(this.connectedEventListener);

        attached.set(true);
    }

    public void detachListener() {
        if (!attached.get()) {
            return;
        }

        this.ref.removeEventListener(this.childEventListener);
        this.ref.removeEventListener(this.valueEventListener);
        //this.connectedRef.removeEventListener(this.connectedEventListener);

        attached.set(false);
    }

    @NonNull
    private Note parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
        Note note = dataSnapshot.getValue(Note.class);
        Objects.requireNonNull(note);

        note.setKey(dataSnapshot.getKey());

        return note;
    }

    @Nullable
    public List<Note> getLatestNoteList() {
        return notes.getValue();
    }

    public void _save(@NonNull Note note, @NonNull SaveTaskCallback callback) {
        if (TextUtils.isEmpty(note.getKey())) {
            ref.push().setValue(note, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    callback.onSuccess(databaseReference.getKey());
                } else {
                    callback.onError(databaseError.toException());
                }
            });
        } else {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(note.getKey(), note.getUpdateMap());
            Task<Void> task = ref.updateChildren(updates);
            task.addOnSuccessListener(aVoid -> callback.onSuccess(note.getKey()));
            task.addOnFailureListener(e -> callback.onError(e));
        }
    }

    public void save(@NonNull Note note) {
        if (TextUtils.isEmpty(note.getKey())) {
            ref.push().setValue(note);
        } else {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(note.getKey(), note.getUpdateMap());
            ref.updateChildren(updates);
        }
    }

    public void _pin(@NonNull Note note, @NonNull TaskCallback callback) {
        if (TextUtils.isEmpty(note.getKey())) {
            Timber.w("cannot update pinned state on new note");
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(note.getKey(), note.getUpdateMap());

        Task<Void> task = ref.updateChildren(updates);
        task.addOnSuccessListener(aVoid -> callback.onSuccess());
        task.addOnFailureListener(e -> callback.onError(e));
    }

    public void pin(@NonNull Note note) {
        if (TextUtils.isEmpty(note.getKey())) {
            Timber.w("cannot update pinned state on new note");
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(note.getKey(), note.getUpdateMap());

        ref.updateChildren(updates);
    }

    public void _delete(@NonNull Note note, @NonNull TaskCallback callback) {
        if (TextUtils.isEmpty(note.getKey())) {
            Timber.w("cannot delete note without key");
            return;
        }
        ref.child(note.getKey()).removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                callback.onSuccess();
            } else {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void delete(@NonNull Note note) {
        if (TextUtils.isEmpty(note.getKey())) {
            Timber.w("cannot delete note without key");
            return;
        }
        ref.child(note.getKey()).removeValue();
    }

    public interface SaveTaskCallback {
        void onSuccess(String key);

        void onError(Exception exception);
    }

    public interface TaskCallback {
        void onSuccess();

        void onError(Exception exception);
    }
}
