package io.viktorot.notefy.repo;

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

    public Observable<String> getColorFilterObservable() {
        return colorFilterRelay.startWith(FILTER_NONE);
    }

    @NonNull
    public String getColorFilter() {
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

//    public Disposable observe(Consumer<String> consumer) {
//        return colorFilterRelay.subscribe(colorFilterRelay);
//    }


}
