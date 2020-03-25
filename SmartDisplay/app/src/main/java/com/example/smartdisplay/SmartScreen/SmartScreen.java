package com.example.smartdisplay.SmartScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.R;
import com.example.smartdisplay.SmartScreen.WeatherHelpers.RemoteFetch;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SmartScreen extends AppCompatActivity {

    //Weather değişkenleri
    Typeface weatherFont;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    Handler handler;

    //
    TextView dateInfo;
    TextView details_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_screen);

        //default başlangıç
        define();
        setContentView();

        //tarih ve hava durumu-her saat tetiklendi
        scheduleRepeat();


    }

    private void define() {
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        //
        dateInfo = findViewById(R.id.dateInfo);
        details_field = findViewById(R.id.details_field);
    }

    public SmartScreen() {
        handler = new Handler();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setContentView() {
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ekranın yan olarak kullanılmasını sağlar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //uygulama dili ingilizce olarak ayarlandıki tarihler ona göre gelsin
        Locale.setDefault(Locale.ENGLISH);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(Locale.ENGLISH);
        configuration.setLayoutDirection(Locale.ENGLISH);
        createConfigurationContext(configuration);
        //////////////////
    }


    /***********************************************/
    //Weather ve Date her saat başlangıcında yenilendi
    public void scheduleRepeat() {
        //ilk açılışta değerler atandı
        getLocationAndstartWeather();
        setDate();

        //her saat başlangıcında tekrar değerler yenilendi
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //konum alınıp weather tetiklendi
                getLocationAndstartWeather();

                //tarih atandı
                setDate();
            }
        };
        timer.schedule(task, millisToNextHour(), 1000 * 60 * 60);
    }

    private long millisToNextHour() {
        LocalDateTime nextHour = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
        return LocalDateTime.now().until(nextHour, ChronoUnit.MILLIS);
    }

    public void setDate() {
        runOnUiThread(new Runnable() {//timer yapısı için gerekli rutin sadece

            @Override
            public void run() {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy");//formating according to my need
                String date = formatter.format(today);
                dateInfo.setText(date);
            }
        });
    }

    public void getLocationAndstartWeather() {////konum alınıp weather tetiklendi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String longitude = location.getLongitude() + "";
            String latitude = location.getLatitude() + "";
            //weather başlatıldı
            updateWeatherData(latitude + "", longitude + "");
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.locationPermission), Toast.LENGTH_LONG).show();
        }
    }

    ////Weather Part
    private void updateWeatherData(final String lat, final String lon) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getApplicationContext(), lat, lon);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            details_field.setText(getString(R.string.controlInternet));
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US));

            currentTemperatureField.setText(
                    (int) ((main.getDouble("temp") - 273.15)) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            Date date = java.util.Calendar.getInstance().getTime();
            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.i("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        int icon = 0;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = R.drawable.ic_weather_sunny;
            } else {
                icon = R.drawable.ic_weather_clear_night;
            }
        } else {
            switch (id) {
                case 2:
                    icon = R.drawable.ic_weather_thunder;
                    break;
                case 3:
                    icon = R.drawable.ic_weather_drizzle;
                    break;
                case 7:
                    icon = R.drawable.ic_weather_foggy;
                    break;
                case 8:
                    icon = R.drawable.ic_weather_cloudy;
                    break;
                case 6:
                    icon = R.drawable.ic_weather_snowy;
                    break;
                case 5:
                    icon = R.drawable.ic_weather_rainy;
                    break;
                default:
                    icon = R.drawable.ic_thermometer;
                    break;
            }
        }
        weatherIcon.setBackground(getDrawable(icon));
    }
    ////Weather Part
    /***********************************************/

}
