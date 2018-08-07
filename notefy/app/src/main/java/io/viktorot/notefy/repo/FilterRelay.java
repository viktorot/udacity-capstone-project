package io.viktorot.notefy.repo;

import android.text.TextUtils;

import com.jakewharton.rxrelay2.BehaviorRelay;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public class FilterRelay {

    private static final String FILTER_NONE = "";

    private final BehaviorRelay<String> colorFilterRelay = BehaviorRelay.create();
    private final BehaviorRelay<Integer> tagFilterRelay = BehaviorRelay.create();

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
