package io.viktorot.notefy.navigator.events;

import androidx.fragment.app.Fragment;

public class Replace implements NavEvent {

    private final Fragment fragment;
    private final String tag;

    public Replace(Fragment fragment, String tag) {
        this.fragment = fragment;
        this.tag = tag;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTag() {
        return tag;
    }
}