package com.example.smartdisplay.SmartScreen.WeatherHelpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.example.smartdisplay.R;

public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";

    public static JSONObject getJSON(Context context, String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, lat,lon,"50a4030ddbb41c21d2c2db6586ddae54"));//ikincisi API key(siteden alınan)
            Log.i("sdfsfsd",url+"");
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            Log.i("sdfsfsd2",data+"");

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            Log.i("sdfsfsd2","hata"+e);
            return null;
        }
    }
}
