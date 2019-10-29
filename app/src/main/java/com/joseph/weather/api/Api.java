package com.joseph.weather.api;

import com.joseph.weather.model.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("weather/")
    Observable<Response> getWeatherByLocation(@Query("lat") double lat, @Query("lon") double lon, @Query("APPID") String appID);
}
