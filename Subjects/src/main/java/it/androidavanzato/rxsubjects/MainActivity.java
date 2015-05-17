package it.androidavanzato.rxsubjects;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends AppCompatActivity {

    private static PointsHolderReplay pointsHolder = new PointsHolderReplay();

    private TextView text;

    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button10).setOnClickListener(v -> pointsHolder.addPoints(10));
        findViewById(R.id.button20).setOnClickListener(v -> pointsHolder.addPoints(Observable.just(20).delay(1, TimeUnit.SECONDS)));

        text = (TextView) findViewById(R.id.text);
    }

    @Override protected void onResume() {
        super.onResume();
        subscriptions = new CompositeSubscription(
                pointsHolder
                        .addPoints(Observable.interval(5, TimeUnit.SECONDS).map(l -> 1)),
                pointsHolder.asObservable(false)
                        .map(PointsEvent::getGainedPoints).filter(p -> p > 0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showToast),
                pointsHolder.asObservable(true)
                        .map(PointsEvent::getPoints).map(Object::toString)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(text::setText)
        );
    }

    @Override protected void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }

    private void showToast(int points) {
        Toast.makeText(this, "You gained " + points + " points!", Toast.LENGTH_SHORT).show();
    }
}
