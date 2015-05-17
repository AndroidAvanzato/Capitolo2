package it.androidavanzato.rxweather.rxweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.androidavanzato.rxweather.rxweather.api.WeatherApiClient;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class MainActivity extends AppCompatActivity {

    public static final String LAT = "LAT";
    public static final String LON = "LON";

    private Subscription subscription;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        subscription = createObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        text::setText,
                        MainActivity.this::showToast
                );

    }

    private void cacheOnPrefs(Pair<Double, Double> pair) {
        prefs.edit()
                .putInt(LAT, (int) (pair.first * 1000000))
                .putInt(LON, (int) (pair.second * 1000000))
                .apply();
    }

    private void showToast(Throwable e) {
        Toast.makeText(MainActivity.this, e.getClass().getSimpleName() + (TextUtils.isEmpty(e.getMessage()) ? "" : ": " + e.getMessage()), Toast.LENGTH_LONG).show();
    }

    private Observable<String> createObservable() {
        return locationObservable()
                .doOnNext(this::cacheOnPrefs)
                .timeout(5, TimeUnit.SECONDS)
                .onExceptionResumeNext(readFromCache())
                .flatMap(location -> Observable.zip(
                                temperatureObservable(location.first, location.second).subscribeOn(Schedulers.io()),
                                geocoderObservable(location.first, location.second).subscribeOn(Schedulers.io()),
                                (temperature, address) -> address.getSubAdminArea() + " - " + temperature + "°"
                        )
                )
                .timeout(10, TimeUnit.SECONDS);
    }

    private Observable<Pair<Double, Double>> readFromCache() {
        return Observable.create(subscriber -> {
            int lat = prefs.getInt(LAT, Integer.MAX_VALUE);
            if (lat == Integer.MAX_VALUE) {
                subscriber.onError(new Exception("No location available"));
            } else {
                int lon = prefs.getInt(LON, Integer.MAX_VALUE);
                subscriber.onNext(new Pair<>(lat / 1000000d, lon / 1000000d));
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Float> temperatureObservable(double lat, double lon) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setExecutors(Runnable::run, null)
                .build();
        WeatherApiClient weatherApiClient = restAdapter.create(WeatherApiClient.class);
        return weatherApiClient.getWeather(lat, lon).map(weatherResponse -> weatherResponse.getTemperature().getTemp());
    }

    private Observable<Address> geocoderObservable(final double lat, final double lon) {
        return Observable.create(new OnSubscribe<Address>() {
            @Override public void call(Subscriber<? super Address> subscriber) {
                try {
                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
                    if (!addresses.isEmpty()) {
                        subscriber.onNext(addresses.get(0));
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new Exception("No address found"));
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<Pair<Double, Double>> locationObservable() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return Observable.create(new OnSubscribe<Location>() {
            @Override public void call(final Subscriber<? super Location> subscriber) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null && isExpired(location)) {
                    subscriber.onNext(location);
                    subscriber.onCompleted();
                } else {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        subscriber.onError(new Exception("No gps available"));
                    } else {
                        final LocationListener locationListener = new LocationListenerAdapter() {
                            @Override public void onLocationChanged(final Location location) {
                                subscriber.onNext(location);
                                subscriber.onCompleted();
                            }
                        };

                        subscriber.add(Subscriptions.create(() -> locationManager.removeUpdates(locationListener)));

                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());
                    }
                }
            }
        }).observeOn(Schedulers.io()).map(location -> new Pair<>(location.getLatitude(), location.getLongitude()));
    }

    private boolean isExpired(Location location) {
        return location.getTime() > System.currentTimeMillis() - 1000 * 60 * 60 * 24;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
