package it.androidavanzato.rxweather.rxweather.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface WeatherApiClient {

    @GET("/weather?units=metric") Observable<WeatherResponse> getWeather(@Query("lat") double lat, @Query("lon") double lon);
}