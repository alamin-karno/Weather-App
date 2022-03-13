package com.alaminkarno.weatherapp.response;

import com.alaminkarno.weatherapp.model.Weather;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("consolidated_weather")
    @Expose()
    private List<Weather> weathers;

    public Weather getWeather() {
        return weathers.size() > 0 ? weathers.get(0) : null;
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "weathers=" + weathers +
                '}';
    }
}
