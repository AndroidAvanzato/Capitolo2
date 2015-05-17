package it.androidavanzato.rxweather.rxweather;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class ExponentialRetry implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private int retryDelayMillis;
    private int retryCount;

    public ExponentialRetry(int maxRetries) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = 1000 + new Random().nextInt(1000);
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts.flatMap(throwable -> {
            if (++retryCount < maxRetries) {
                retryDelayMillis *= 2;
                return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
            }

            return Observable.error(throwable);
        });
    }
}