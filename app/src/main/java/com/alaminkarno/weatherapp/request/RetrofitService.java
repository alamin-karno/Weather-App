package com.alaminkarno.weatherapp.request;

import com.alaminkarno.weatherapp.view.utils.Credential;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Credential.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static WeatherApi weatherApi = retrofit.create(WeatherApi.class);

    public static WeatherApi getWeatherApi(){
        return weatherApi;
    }

}
