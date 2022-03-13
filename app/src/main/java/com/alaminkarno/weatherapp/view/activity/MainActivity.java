package com.alaminkarno.weatherapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alaminkarno.weatherapp.R;
import com.alaminkarno.weatherapp.model.LocationModel;
import com.alaminkarno.weatherapp.model.Weather;
import com.alaminkarno.weatherapp.request.RetrofitService;
import com.alaminkarno.weatherapp.request.WeatherApi;
import com.alaminkarno.weatherapp.response.WeatherResponse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private TextView tempTV,feelTempTv,pressureTv,
            maxTempTV,minTempTv,windSpeedTv,
            humidityTV,weatherStateTV,locationTV;
    private LinearLayout mainBackground;
    private ProgressBar loading;
    private EditText searchET;
    private Button searchBTN;
    private String searchString;
    private String temp,pressure,maxTemp,
            minTemp,windSpeed,humidity,city;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getRetrofitLocationResponse();


        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchString = searchET.getText().toString().trim();

                if(searchString.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter any city name", Toast.LENGTH_SHORT).show();
                }
                else{
                    getWeatherDetails(searchString);
                }
            }
        });

    }

    private void getRetrofitLocationResponse() {

        getLocation();

    }

    private void getLocation() {

        loading.setVisibility(View.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try{
                String fullCity = getCityName(location.getLatitude(), location.getLongitude());
                city = fullCity.substring(0,fullCity.indexOf(' '));

                if(city.isEmpty()){
                    getWeatherDetails("Dhaka");
                }
                else{
                    getWeatherDetails(city);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    String fullCity = getCityName(location.getLatitude(), location.getLongitude());

                    city = fullCity.substring(0, fullCity.indexOf(' '));

                    if (city.isEmpty()) {
                        getWeatherDetails("Dhaka");
                    } else {
                        getWeatherDetails(city);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Location Permission Denied!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCityName(double lat, double lon) {

        String cityName = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lat, lon, 10);
            if (addressList.size() > 0) {
                for (Address address : addressList) {


                    if (address.getLocality() != null && address.getLocality().length() > 0) {
                        cityName = address.getLocality();
                        Log.d("after:",address.getLocality());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherDetails(String city) {

        loading.setVisibility(View.VISIBLE);

        WeatherApi weatherApi = RetrofitService.getWeatherApi();
        Call<List<LocationModel>> responseCall = weatherApi.getLocation(city);


        responseCall.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                 if(response.code() == 200){

                     Log.d("weather","==========Enqueue============"+response.body());


                     List<LocationModel> locationModels = new ArrayList<>(response.body());

                     if(locationModels.size() > 0){
                         locationTV.setText(locationModels.get(0).getTitle());
                         getRetrofitWeatherResponse(locationModels.get(0).getWoeid());
                     }
                     else{
                         locationTV.setText("Dhaka");
                         getRetrofitWeatherResponse(1915035);
                     }


                 }

            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
                Log.d("weather","==========Failed============"+t.toString());
            }
        });

    }



    private void getRetrofitWeatherResponse(int woe_id) {

        WeatherApi weatherApi = RetrofitService.getWeatherApi();
        Call<WeatherResponse> responseCall = weatherApi.getWeather(woe_id);

        responseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                if(response.code() == 200){

                    loading.setVisibility(View.INVISIBLE);

                    Weather weather = response.body().getWeather();

                    Log.d("weather: ","Temp: "+weather.getTheTemp());

                    temp = (String) DECIMAL_FORMAT.format(weather.getTheTemp());
                    tempTV.setText(temp);
                    maxTemp = (String) DECIMAL_FORMAT.format(weather.getMaxTemp());
                    maxTempTV.setText(maxTemp+" °C");
                    minTemp = (String) DECIMAL_FORMAT.format(weather.getMinTemp());
                    minTempTv.setText(minTemp+" °C");
                    feelTempTv.setText(temp+" °C");
                    pressure = (String) DECIMAL_FORMAT.format(weather.getAirPressure());
                    pressureTv.setText(pressure+" mmHg");
                    windSpeed = (String) DECIMAL_FORMAT.format(weather.getWindSpeed());
                    windSpeedTv.setText(windSpeed+" km/h");
                    humidity = (String) DECIMAL_FORMAT.format(weather.getHumidity());
                    humidityTV.setText(humidity+"%");
                    String bg = weather.getWeatherStateName().replaceAll(" ","").toLowerCase();
                    setBackground(bg);
                    weatherStateTV.setText(weather.getWeatherStateName());


                }


            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

                loading.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setBackground(String bg) {
        if(bg.equals("clear")){
            mainBackground.setBackgroundResource(R.drawable.clear);
        }
        else if(bg.equals("hail")){
            mainBackground.setBackgroundResource(R.drawable.hail);
        }
        else if(bg.equals("heavycloud")){
            mainBackground.setBackgroundResource(R.drawable.heavycloud);
        }
        else if(bg.equals("heavyrain")){
            mainBackground.setBackgroundResource(R.drawable.heavyrain);
        }
        else if(bg.equals("lightcloud")){
            mainBackground.setBackgroundResource(R.drawable.lightcloud);
        }
        else if(bg.equals("lightrain")){
            mainBackground.setBackgroundResource(R.drawable.lightrain);
        }
        else if(bg.equals("showers")){
            mainBackground.setBackgroundResource(R.drawable.showers);
        }
        else if(bg.equals("sky")){
            mainBackground.setBackgroundResource(R.drawable.sky);
        }
        else if(bg.equals("sleet")){
            mainBackground.setBackgroundResource(R.drawable.sleet);
        }
        else if(bg.equals("snow")){
            mainBackground.setBackgroundResource(R.drawable.snow);
        }
        else if(bg.equals("thunderstorm")){
            mainBackground.setBackgroundResource(R.drawable.thunderstorm);
        }
    }


    private void init() {

        tempTV = findViewById(R.id.tempTV);
        feelTempTv = findViewById(R.id.feelTempTV);
        pressureTv = findViewById(R.id.pressureTV);
        maxTempTV = findViewById(R.id.maxTempTV);
        minTempTv = findViewById(R.id.minTempTV);
        windSpeedTv = findViewById(R.id.windSpeedTV);
        humidityTV = findViewById(R.id.humidityTV);
        weatherStateTV = findViewById(R.id.weather_stateTV);
        locationTV = findViewById(R.id.locationTV);

        mainBackground = findViewById(R.id.backgroundLL);
        loading = findViewById(R.id.loadingBar);

        searchET = findViewById(R.id.searchET);
        searchBTN = findViewById(R.id.searchBTN);
    }

    public void refreshMyLocation(View view) {

        getLocation();
    }

    public void sideMenu(View view) {
        Toast.makeText(this, "Under Development...", Toast.LENGTH_SHORT).show();
    }
}