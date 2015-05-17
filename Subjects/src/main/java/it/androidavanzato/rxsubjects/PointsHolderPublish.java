package it.androidavanzato.rxsubjects;

import rx.Observable;
import rx.Subscription;
import rx.functions.Actions;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class PointsHolderPublish {

    private int points;

    private Subject<PointsEvent, PointsEvent> pointsSubject = PublishSubject.create();

    public void addPoints(int points) {
        addPoints(Observable.just(points));
    }

    private Integer getCurPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Observable<PointsEvent> asObservable(boolean emitCurrentValue) {
        if (emitCurrentValue) {
            return pointsSubject.asObservable().startWith(new PointsEvent(points, 0));
        } else {
            return pointsSubject.asObservable();
        }
    }

    public Subscription addPoints(Observable<Integer> observable) {
        return observable
                .doOnNext(points -> setPoints(getCurPoints() + points))
                .map(points -> new PointsEvent(getCurPoints(), points))
                .subscribe(pointsSubject::onNext, Actions.empty());
    }
}
