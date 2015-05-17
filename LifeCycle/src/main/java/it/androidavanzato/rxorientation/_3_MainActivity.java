package it.androidavanzato.rxorientation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class _3_MainActivity extends AppCompatActivity {

    public static final String ITEMS = "items";

    private StringAdapter adapter;

    private RetainedObservableFragment<ArrayList<String>> retainedFragment;

    private Subscription subscription;

    @InjectView(R.id.list) ListView listView;

    @InjectView(R.id.progress) ProgressBar progressBar;

    @InjectView(R.id.error_text) TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        ButterKnife.inject(this);
        adapter = new StringAdapter(this);
        listView.setAdapter(adapter);

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
        subscription = retainedFragment.get().subscribe(new Subscriber<ArrayList<String>>() {
            @Override public void onStart() {
                progressBar.setVisibility(VISIBLE);
                listView.setVisibility(GONE);
                errorText.setVisibility(GONE);
            }

            @Override public void onCompleted() {
                progressBar.setVisibility(GONE);
                errorText.setVisibility(GONE);
                listView.setVisibility(VISIBLE);
                retainedFragment.clear();
            }

            @Override public void onError(Throwable e) {
                progressBar.setVisibility(GONE);
                errorText.setVisibility(VISIBLE);
                listView.setVisibility(GONE);
            }

            @Override public void onNext(ArrayList<String> l) {
                adapter.replaceData(l);
            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }
}
