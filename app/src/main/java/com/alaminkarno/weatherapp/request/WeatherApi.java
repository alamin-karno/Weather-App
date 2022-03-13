package com.alaminkarno.weatherapp.request;

import com.alaminkarno.weatherapp.model.LatLon;
import com.alaminkarno.weatherapp.model.LocationModel;
import com.alaminkarno.weatherapp.response.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("search/")
    Call<List<LocationModel>> getLocation(
            @Query("query") String queryData
    );

    @GET("{woeid}")
    Call<WeatherResponse> getWeather(
            @Path("woeid") int woe_id
    );

    @GET("search/")
    Call<List<LatLon>> getLatLon(
            @Query("lattlong") float lat_long
    );
}
