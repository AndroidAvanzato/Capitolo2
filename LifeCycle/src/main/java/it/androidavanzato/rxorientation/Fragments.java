package it.androidavanzato.rxorientation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import rx.functions.Func0;

public class Fragments {
    public static <F extends Fragment> F getOrCreate(FragmentActivity activity, String tag, Func0<F> factory) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        F fragment = (F) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = factory.call();
            fragmentManager.beginTransaction().add(fragment, tag).commit();
        }
        return fragment;
    }
}
