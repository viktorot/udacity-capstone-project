package io.viktorot.notefy.repo;

import android.nfc.Tag;
import android.text.TextUtils;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FilterRelay {

    private static final String FILTER_NONE = "";

    private BehaviorRelay<String> colorFilterRelay = BehaviorRelay.create();
    private BehaviorRelay<Integer> tagFilterRelay = BehaviorRelay.create();

    public Observable<String> getColorFilterObservable() {
        return colorFilterRelay.startWith(FILTER_NONE);
    }

    @NonNull
    public String getActiveColorFilter() {
        String value = colorFilterRelay.getValue();
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    public void postColor(String value) {
        colorFilterRelay.accept(value);
    }

    public Observable<Integer> getTagFilterObservable() {
        return tagFilterRelay.startWith(TagRepo.ID_NONE);
    }

    public int getActiveTagFilter() {
        Integer value = tagFilterRelay.getValue();
        if (value == null) {
            return TagRepo.ID_NONE;
        } else {
            return value;
        }
    }

    public void postTag(int id) {
        tagFilterRelay.accept(id);
    }


}
