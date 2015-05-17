package it.androidavanzato.rxorientation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class _2_MainActivity extends AppCompatActivity {

    public static final String ITEMS = "items";

    private StringAdapter adapter;

    private RetainedObservableFragment<ArrayList<String>> retainedFragment;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        adapter = new StringAdapter(this);
        ((ListView) findViewById(R.id.list)).setAdapter(adapter);

        retainedFragment = Fragments.getOrCreate(this, "retained", RetainedObservableFragment::new);

        if (!retainedFragment.isRunning()) {
            ArrayList<String> savedList = savedInstanceState == null ? null : savedInstanceState.getStringArrayList(ITEMS);
            if (savedList != null && !savedList.isEmpty()) {
                adapter.replaceData(savedList);
            } else {
                Observable<ArrayList<String>> observable = StringArrayDataLoader.loadData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                retainedFragment.bind(observable.replay());
            }
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(ITEMS, adapter.getItems());
    }

    @Override protected void onResume() {
        super.onResume();
        subscription = retainedFragment.get().subscribe(l -> {
            adapter.replaceData(l);
            retainedFragment.clear();
        });
    }

    @Override protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }
}
