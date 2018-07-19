package io.viktorot.notefy.navigator;

import androidx.fragment.app.Fragment;

public class Push implements Command {

    private final Fragment fragment;
    private final String tag;

    public Push(Fragment fragment, String tag) {
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
