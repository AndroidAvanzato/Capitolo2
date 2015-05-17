package it.androidavanzato.rxorientation;

import android.support.v4.app.Fragment;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Actions;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class RetainedObservableFragment4<T> extends Fragment {

    private Subscription connectableSubscription = Subscriptions.empty();

    private ConnectableObservable<T> observable;

    private Func0<Subscriber<T>> subscriberFactory;

    public RetainedObservableFragment4() {
        setRetainInstance(true);
    }

    public Subscription bind(ConnectableObservable<T> observable) {
        this.observable = observable;
        connectableSubscription = observable.connect();
        if (subscriberFactory != null) {
            return new CompositeSubscription(
                    observable.subscribe(Actions.empty(), t -> clear(), this::clear),
                    observable.subscribe(subscriberFactory.call())
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
