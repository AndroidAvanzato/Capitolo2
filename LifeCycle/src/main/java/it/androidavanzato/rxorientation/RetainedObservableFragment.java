package it.androidavanzato.rxorientation;

import android.support.v4.app.Fragment;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;

public class RetainedObservableFragment<T> extends Fragment {

    private Subscription connectableSubscription = Subscriptions.empty();

    private ConnectableObservable<T> observable;

    public RetainedObservableFragment() {
        setRetainInstance(true);
    }

    public void bind(ConnectableObservable<T> observable) {
        this.observable = observable;
        connectableSubscription = observable.connect();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        connectableSubscription.unsubscribe();
    }

    public Observable<T> get() {
        if (observable == null) {
            return Observable.empty();
        }
        return observable;
    }

    public boolean isRunning() {
        return observable != null;
    }

    public void clear() {
        observable = null;
        connectableSubscription = Subscriptions.empty();
    }
}
