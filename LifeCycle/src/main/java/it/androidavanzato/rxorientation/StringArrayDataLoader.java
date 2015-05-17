package it.androidavanzato.rxorientation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

public class StringArrayDataLoader {
    public static Observable<ArrayList<String>> loadData() {
        return Observable.just(new ArrayList<>(Arrays.asList("A", "B", "C"))).delay(3, TimeUnit.SECONDS).doOnNext(new Action1<ArrayList<String>>() {
            @Override public void call(ArrayList<String> strings) {
//                System.out.println(1/0);
            }
        });
    }
}
