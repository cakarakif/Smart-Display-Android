package com.example.smartdisplay.SmartScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.R;
import com.example.smartdisplay.SmartScreen.WeatherHelpers.CityPreference;
import com.example.smartdisplay.SmartScreen.WeatherHelpers.RemoteFetch;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class SmartScreen extends AppCompatActivity {

    //Weather değişkenleri
    Typeface weatherFont;
    TextView cityField;
    //TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    Handler handler;

    public SmartScreen(){
        handler = new Handler();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_screen);

        define();
        setContentView();

        updateWeatherData(new CityPreference(this).getCity());

    }

    private void define(){
        cityField = findViewById(R.id.city_field);
        //updatedField = findViewById(R.id.updated_field);
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setContentView(){
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ekranın yan olarak kullanılmasını sağlar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    ////Weather Part
    public void changeCity(String city){
        updateWeatherData(city);
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getApplicationContext(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) );
                            //"\n" + "Humidity: " + main.getString("humidity") + "%" +
                            //"\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    (int)((main.getDouble("temp")-273.15)) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            Date date=java.util.Calendar.getInstance().getTime();
            //String updatedOn = df.format(date);
            //updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.i("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        int icon=0;
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = R.drawable.ic_weather_sunny;
            } else {
                icon = R.drawable.ic_weather_clear_night;
            }
        } else {
            switch(id) {
                case 2 : icon = R.drawable.ic_weather_thunder;
                    break;
                case 3 : icon = R.drawable.ic_weather_drizzle;
                    break;
                case 7 : icon = R.drawable.ic_weather_foggy;
                    break;
                case 8 : icon = R.drawable.ic_weather_cloudy;
                    break;
                case 6 : icon = R.drawable.ic_weather_snowy;
                    break;
                case 5 : icon = R.drawable.ic_weather_rainy;
                    break;
                default: icon= R.drawable.ic_thermometer;
                    break;
            }
        }
        weatherIcon.setBackground(getDrawable(icon));
    }
    ////Weather Part

}
