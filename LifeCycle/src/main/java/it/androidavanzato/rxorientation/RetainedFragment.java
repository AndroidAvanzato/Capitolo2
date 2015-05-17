package it.androidavanzato.rxorientation;

import android.support.v4.app.Fragment;

public class RetainedFragment<T> extends Fragment {

    private T object;

    public RetainedFragment() {
        setRetainInstance(true);
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }
}
