package com.example.smartdisplay.SmartScreen.WeatherHelpers;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    public String getLat(){
        return prefs.getString("lat", "38.418417");
    }

    public void setLat(String lat){
        prefs.edit().putString("lat", lat).commit();
    }


    public String getLon(){
        return prefs.getString("lon", "27.140107");
    }

    public void setLon(String lon){
        prefs.edit().putString("lon", lon).commit();
    }

}

