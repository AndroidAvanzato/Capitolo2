package it.androidavanzato.rxorientation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;


public class _0_MainActivity extends AppCompatActivity {

    private StringAdapter adapter;

    private RetainedFragment<Observable<ArrayList<String>>> retainedFragment;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        adapter = new StringAdapter(this);
        ((ListView) findViewById(R.id.list)).setAdapter(adapter);

        retainedFragment = Fragments.getOrCreate(this, "retained", RetainedFragment::new);

        if (retainedFragment.get() == null) {
            Observable<ArrayList<String>> observable = StringArrayDataLoader.loadData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

            ConnectableObservable<ArrayList<String>> replayObservable = observable.replay();
            replayObservable.connect();
            retainedFragment.set(replayObservable);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        subscription = retainedFragment.get().subscribe(adapter::replaceData);
    }

    @Override protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }
}
