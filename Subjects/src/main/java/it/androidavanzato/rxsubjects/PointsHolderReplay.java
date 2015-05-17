package it.androidavanzato.rxsubjects;

import rx.Observable;
import rx.Subscription;
import rx.functions.Actions;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class PointsHolderReplay {

    private Subject<PointsEvent, PointsEvent> pointsSubject = ReplaySubject.createWithSize(1);

    public PointsHolderReplay() {
        pointsSubject = ReplaySubject.createWithSize(1);
        pointsSubject.onNext(new PointsEvent(0, 0));
    }

    public void addPoints(int points) {
        addPoints(Observable.just(points));
    }

    private Integer getPoints() {
        return pointsSubject.map(PointsEvent::getPoints).toBlocking().first();
    }

    public Observable<PointsEvent> asObservable(boolean emitCurrentValue) {
        if (emitCurrentValue) {
            return pointsSubject.asObservable();
        } else {
            return pointsSubject.asObservable().skip(1);
        }
    }

    public Subscription addPoints(Observable<Integer> observable) {
        return observable
                .map(points -> new PointsEvent(getPoints() + points, points))
                .subscribe(pointsSubject::onNext, Actions.empty());
    }
}
