package it.androidavanzato.rxorientation;

import android.support.v4.app.Fragment;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Actions;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class RetainedObservableFragment5<T> extends Fragment {

    private Subscription connectableSubscription = Subscriptions.empty();

    private ConnectableObservable<T> observable;

    private Func0<Subscriber<T>> subscriberFactory;

    public RetainedObservableFragment5() {
        setRetainInstance(true);
    }

    public Subscription bind(Observable<T> observable) {
        this.observable = observable.replay();
        connectableSubscription = this.observable.connect();
        if (subscriberFactory != null) {
            return new CompositeSubscription(
                    this.observable.subscribe(Actions.empty(), t -> clear(), this::clear),
                    this.observable.subscribe(subscriberFactory.call())
            );
        } else {
            return Subscriptions.empty();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        connectableSubscription.unsubscribe();
    }

    public boolean isRunning() {
        return observable != null;
    }

    private void clear() {
        observable = null;
        connectableSubscription = Subscriptions.empty();
    }

    public Subscription initFactory(Func0<Subscriber<T>> subscriberFactory) {
        this.subscriberFactory = subscriberFactory;
        CompositeSubscription subscriptions = new CompositeSubscription();
        if (observable != null) {
            subscriptions.add(observable.subscribe(Actions.empty(), t -> clear(), this::clear));
            subscriptions.add(observable.subscribe(subscriberFactory.call()));
        }
        subscriptions.add(Subscriptions.create(() -> this.subscriberFactory = null));
        return subscriptions;
    }

}
