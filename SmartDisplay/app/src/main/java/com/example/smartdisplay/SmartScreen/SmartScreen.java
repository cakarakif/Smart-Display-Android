package com.example.smartdisplay.SmartScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.Adapter.SmartScreenTaskListAdapter;
import com.example.smartdisplay.Adapter.TaskListAdapter;
import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import com.example.smartdisplay.SmartScreen.ExchangeHelper.CurrencyExchange;
import com.example.smartdisplay.SmartScreen.ExchangeHelper.CurrencyExchangeService;
import com.example.smartdisplay.SmartScreen.News.Adapter;
import com.example.smartdisplay.SmartScreen.News.ApiClient;
import com.example.smartdisplay.SmartScreen.News.Model.Articles;
import com.example.smartdisplay.SmartScreen.News.Model.Headlines;
import com.example.smartdisplay.SmartScreen.ShowVideo.show_video;
import com.example.smartdisplay.SmartScreen.WeatherHelpers.RemoteFetch;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    private TextView oneName,oneDate, twoName,twoDate, threeName,threeDate;
    private LinearLayout one,two,three;
    private Calendar cal;

    //
    private ListView taskListView;
    private List<UserTask> taskList, filteredList;
    private SmartScreenTaskListAdapter listAdapter;
    private DatabaseProcessing dtbs;

    //news
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String country;
    Dialog dialog;
    final String API_KEY = "ded3c51db78c4bbabeccf10ddd008af4";
    Adapter adapter;
    List<Articles>  articles = new ArrayList<>();

    //exchange
    TextView exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_screen);

        //default başlangıç
        define();
        setContentView();
        routing();

        //tasklar alındı ve view'e atandı
        readUserTasks();

        //newsPart
        startNews();

        //tarih ve hava durumu-her saat tetiklendi
        scheduleRepeat();

        //startExchange
        startExchange();

    }

    private void define() {
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        //
        dateInfo = findViewById(R.id.dateInfo);
        details_field = findViewById(R.id.details_field);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);

        oneName = findViewById(R.id.oneName);
        oneDate = findViewById(R.id.oneDate);
        twoName = findViewById(R.id.twoName);
        twoDate = findViewById(R.id.twoDate);
        threeName = findViewById(R.id.threeName);
        threeDate = findViewById(R.id.threeDate);

        cal = Calendar.getInstance();
        dtbs=new DatabaseProcessing(this);
        taskListView =findViewById(R.id.taskListView);

        //
        exchange = findViewById(R.id.exchange);

    }

    private void routing(){
        //temel task calendarı baslangıcı yapıldı
        dateCalendar(0);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(-1);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(0);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(1);
            }
        });
    }

    private void dateCalendar(int numberMov){
        //seçilen tarihe göre ayarlama ve atamalar yapıldı.
        //number kaç ileri-geri gittiği alınarak sabit mantık işletildi.

        Locale.setDefault(Locale.ENGLISH);

        cal.add(cal.DATE, numberMov);
        twoDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        twoName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        //Eğer bugünün günü seçilmediyse üste o tarih yazılır.
        if(cal.get(cal.DAY_OF_MONTH) == Calendar.getInstance().get(cal.DAY_OF_MONTH) &&
                (cal.get(cal.MONTH)+1) == (Calendar.getInstance().get(cal.MONTH)+1) &&
                cal.get(cal.YEAR) == Calendar.getInstance().get(cal.YEAR)){

            twoName.setText(getString(R.string.today));
        }

        //ileri bir gün
        cal.add(cal.DATE, 1);
        threeDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        threeName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        //geri iki gün
        cal.add(cal.DATE, -2);
        oneDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        oneName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());


        //default almak için ikinciye çekildi
        cal.add(cal.DATE, 1);

        //her seferinde liste üzerinden yeniden filtreleme yapıldı
        filterList();
    }

    //her saat başlangıcında yenilenen yapılar
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

                //task calendarda kontrol edildi/resetlendi
                cal = Calendar.getInstance();
                dateCalendar(0);

                //news yenilendi
                retrieveJson("",country,API_KEY);
            }
        };
        timer.schedule(task, millisToNextHour(), 1000 * 60 * 60);
    }

    private void readUserTasks(){//task bilgisi DatabaseProcessingden sonra burası tetiklenir

        dtbs.readUserTasks();//okuma için tetiklendi


        try {//tetiklenen işlem postvalue olunca burası tetiklenir
            dtbs.getUserTasks().observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        //verilerimizi aldık
                        taskList = new ArrayList<>();

                        //tüm tasklar alındı.
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            UserTask usrtasks = postSnapshot.getValue(UserTask.class);
                            taskList.add(usrtasks);
                        }

                        filterList();
                    }else{//eğer null ise boş olarak gösterildi
                        taskList = new ArrayList<>();
                        filteredList = new ArrayList<>();
                        startListView();
                    }
                }
            });
        }catch (Exception e){
        }
    }

    private void filterList() {//listeyi seçili tarihe göre filtreleme
        if (taskList != null) {
            filteredList = new ArrayList<>();

            for (UserTask list : taskList) {

                //gün olarak seçilenlerin, seçili günde olup olmadığına bakılıp eklendi.
                if (list.getIsActive() && list.getRepeatType() && (list.getRepeatInfo())
                        .contains((cal.getDisplayName(cal.DAY_OF_WEEK, cal.SHORT, Locale.getDefault())).substring(0, 3))) {
                    filteredList.add(list);
                }

                //tarih seçilenler kontrol edilerek eklendi.
                if (list.getIsActive() && !list.getRepeatType()) {
                    String[] dates = list.getRepeatInfo().split("/");

                    if (cal.get(cal.DAY_OF_MONTH) == Integer.parseInt(dates[0]) &&
                            (cal.get(cal.MONTH) + 1) == Integer.parseInt(dates[1]) &&
                            cal.get(cal.YEAR) == Integer.parseInt(dates[2])) {
                        filteredList.add(list);
                    }
                }
            }

            //liste default olarak time'a göre sıralı gelsin
            Collections.sort(filteredList, (p1, p2) -> (p1.getHours() - p2.getHours())*60+p1.getMinutes() - p2.getMinutes());

            //liste başlatıldı.
            startListView();
        }
    }

    private void startListView(){

        //list'in nasıl görüneceğinin adapterı
        listAdapter = new SmartScreenTaskListAdapter(filteredList, getApplicationContext());

        //bağlama işlemi yaptık
        taskListView.setAdapter(listAdapter);
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
            String longitude = "28.979530";//default olarak istanbul location atandı
            String latitude = "41.015137";

            if( location != null){
                longitude = location.getLongitude() + "";
                latitude = location.getLatitude() + "";
            }
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

    ////News Part
    private void startNews(){
        country = getCountry();
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        dialog = new Dialog(SmartScreen.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("",country,API_KEY);
            }
        });
        retrieveJson("",country,API_KEY);
    }

    public void retrieveJson(String query ,String country, String apiKey){


        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        call= ApiClient.getInstance().getApi().getHeadlines(country,apiKey);

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(SmartScreen.this,articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(SmartScreen.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry(){
        String country = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        if ( country.equals(""))
            return "us";
        return country.toLowerCase();
    }

    /////News Part
    /***********************************************/

    ////Exchange Part
    private void startExchange(){

        getExcahangeData();

        //animation part
        Animation animationToLeft = new TranslateAnimation(400, -400, 0, 0);
        animationToLeft.setDuration(15000);
        animationToLeft.setRepeatMode(Animation.RESTART);
        animationToLeft.setRepeatCount(Animation.INFINITE);
        exchange .startAnimation(animationToLeft);//your_view for which you need animation
    }

    private void getExcahangeData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.exchangeratesapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrencyExchangeService currencyInterface = retrofit.create(CurrencyExchangeService.class);
        Call<CurrencyExchange> call = currencyInterface.getCurrency();
        call.enqueue(new Callback<CurrencyExchange>() {
            @Override
            public void onResponse(Call<CurrencyExchange> call, Response<CurrencyExchange> response) {
                CurrencyExchange.Rates crrncyExchange = response.body().getRates();

                DecimalFormat df = new DecimalFormat("#.##");

                String content = "";
                content +=  "USD: "+df.format(Double.valueOf(crrncyExchange.getUSD())) +"$ ";
                content +=  "TRY: "+df.format(Double.valueOf(crrncyExchange.getTRY()))+" USD ";
                content +=  "EUR: "+df.format(Double.valueOf(crrncyExchange.getEUR()))+" USD ";
                content +=  "CHF: "+df.format(Double.valueOf(crrncyExchange.getCHF()))+" USD ";
                content +=  "GBP: "+df.format(Double.valueOf(crrncyExchange.getGBP()))+" USD ";
                content +=  "RUB: "+df.format(Double.valueOf(crrncyExchange.getRUB()))+" USD ";
                content +=  "JPY: "+df.format(Double.valueOf(crrncyExchange.getJPY()))+" USD ";
                content +=  "SEK: "+df.format(Double.valueOf(crrncyExchange.getSEK()))+" USD ";
                content +=  "CAD: "+df.format(Double.valueOf(crrncyExchange.getCAD()))+" USD ";

                exchange.setText(content);
            }

            @Override
            public void onFailure(Call<CurrencyExchange> call, Throwable t) {
                Log.i("akfControl",t.getMessage()+"");
                //textViewResult.setText(t.getMessage());
            }
        });
    }

    /////Exchange Part
    /***********************************************/
}
