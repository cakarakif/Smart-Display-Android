package com.example.smartdisplay.ui.add;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.smartdisplay.R;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {
    private View root;

    private CheckBox repeatLogo, onceLogo, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TextView selectTime, timeText, repeatText, onceText, selectDate,dateText;
    private RadioGroup typeRadios;
    private EditText typeEdit;
    private ScrollView scroll;
    private LinearLayout days, dateArea;
    private ImageView dateLogo;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add, container, false);

        define();
        routing();

        return root;
    }

    private void define(){
        repeatLogo=root.findViewById(R.id.repeatLogo);
        monday=root.findViewById(R.id.monday);
        tuesday=root.findViewById(R.id.tuesday);
        wednesday=root.findViewById(R.id.wednesday);
        thursday=root.findViewById(R.id.thursday);
        friday=root.findViewById(R.id.friday);
        saturday=root.findViewById(R.id.saturday);
        sunday=root.findViewById(R.id.sunday);

        selectTime=root.findViewById(R.id.selectTime);
        timeText=root.findViewById(R.id.timeText);

        typeRadios=root.findViewById(R.id.typeRadio);
        typeEdit=root.findViewById(R.id.typeEdit);

        scroll=root.findViewById(R.id.scroll);
        onceLogo=root.findViewById(R.id.onceLogo);

        days=root.findViewById(R.id.days);
        dateArea=root.findViewById(R.id.dateArea);
        repeatText=root.findViewById(R.id.repeatText);
        onceText=root.findViewById(R.id.onceText);

        dateLogo=root.findViewById(R.id.dateLogo);
        selectDate=root.findViewById(R.id.selectDate);
        dateText=root.findViewById(R.id.dateText);
        //ilk açılışta datetext'i için tarih çekildi.
        final Calendar cldr = Calendar.getInstance();
        selectDate.setText(cldr.get(Calendar.DAY_OF_MONTH)+"/"+ (cldr.get(Calendar.MONTH)+1)+"/"+cldr.get(Calendar.YEAR));



    }

    private void routing(){
        repeatLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(repeatLogo.isChecked()){

                    days.setVisibility(View.VISIBLE);
                    dateArea.setVisibility(View.INVISIBLE);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    onceLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    onceLogo.setChecked(false);
                }else {

                    days.setVisibility(View.INVISIBLE);
                    dateArea.setVisibility(View.VISIBLE);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    onceLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    onceLogo.setChecked(true);
                }
            }
        });

        onceLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onceLogo.isChecked()){

                    days.setVisibility(View.INVISIBLE);
                    dateArea.setVisibility(View.VISIBLE);
                    onceLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    repeatLogo.setChecked(false);

                }else {

                    days.setVisibility(View.VISIBLE);
                    dateArea.setVisibility(View.INVISIBLE);
                    onceLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    repeatLogo.setChecked(true);
                }
            }
        });

        repeatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatLogo.performClick();
            }
        });

        onceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onceLogo.performClick();
            }
        });

        daysClicked();//hangi günlerin seçildiği dinlenildi.

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();
            }
        });

        typeRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                scroll.smoothScrollTo(0, scroll.getBottom());//save buttonu gözükmesi için scroll yapıldı

                int selectedRadio = typeRadios.getCheckedRadioButtonId();

                if(selectedRadio == R.id.radioOne){
                    typeEdit.setVisibility(View.INVISIBLE);
                }else if(selectedRadio == R.id.radioTwo){
                    typeEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        dateLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

    }

    private void daysClicked(){
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monday.isChecked()){
                    setBackgroundCB(monday,R.drawable.daymm);
                }else {
                    setBackgroundCB(monday,R.drawable.daym);
                }
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tuesday.isChecked()){
                    setBackgroundCB(tuesday,R.drawable.daytt);
                }else {
                    setBackgroundCB(tuesday,R.drawable.dayt);
                }
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wednesday.isChecked()){
                    setBackgroundCB(wednesday,R.drawable.dayww);
                }else {
                    setBackgroundCB(wednesday,R.drawable.dayw);
                }
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thursday.isChecked()){
                    setBackgroundCB(thursday,R.drawable.daytt);
                }else {
                    setBackgroundCB(thursday,R.drawable.dayt);
                }
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friday.isChecked()){
                    setBackgroundCB(friday,R.drawable.dayff);
                }else {
                    setBackgroundCB(friday,R.drawable.dayf);
                }
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saturday.isChecked()){
                    setBackgroundCB(saturday,R.drawable.dayss);
                }else {
                    setBackgroundCB(saturday,R.drawable.days);
                }
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sunday.isChecked()){
                    setBackgroundCB(sunday,R.drawable.dayss);
                }else {
                    setBackgroundCB(sunday,R.drawable.days);
                }
            }
        });
    }

    private void setBackgroundCB(CheckBox whichone, int background){//genelleştirme için yardımcı fonksiyon
        whichone.setBackgroundResource(background);
    }

    /*
    private void checkRepeatCB(){//eğer tüm günler seçiliyse repeat checkboxı doldurulur.//Şuan Kullanılmıyor
        if(monday.isChecked() && tuesday.isChecked() && wednesday.isChecked()
                              && thursday.isChecked() && friday.isChecked() && saturday.isChecked() && sunday.isChecked()){
            repeatLogo.setChecked(true);
            setBackgroundCB(repeatLogo,R.drawable.ic_radiofill);
        }else{
            repeatLogo.setChecked(false);
            setBackgroundCB(repeatLogo,R.drawable.ic_radioempty);
        }
    }
    */

    /*
    private void setAllDaysClicked(){//Repeat buttonu için işlem atandı.//Şuan Kullanılmıyor
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);
        sunday.setChecked(true);

        setBackgroundCB(monday,R.drawable.daymm);
        setBackgroundCB(tuesday,R.drawable.daytt);
        setBackgroundCB(wednesday,R.drawable.dayww);
        setBackgroundCB(thursday,R.drawable.daytt);
        setBackgroundCB(friday,R.drawable.dayff);
        setBackgroundCB(saturday,R.drawable.dayss);
        setBackgroundCB(sunday,R.drawable.dayss);
    }
    */

    private void selectTime(){
        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_timepicker,null);
        //

        TimePicker picker;
        Button btnGet;;

        picker=(TimePicker)view.findViewById(R.id.datePicker);
        picker.setIs24HourView(true);
        btnGet=(Button)view.findViewById(R.id.select);

        //seçileni time picker başlangı olarak atadık.
        String[] times = selectTime.getText().toString().split(":");
        picker.setHour(Integer.parseInt(times[0]));
        picker.setMinute(Integer.parseInt(times[1]));


        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        //dialog dissmiss edilirse burası çalışır.
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                selectTime.setText(picker.getHour() +":"+ picker.getMinute());
            }
        });
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();
        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.60), (int)(getResources().getDisplayMetrics().heightPixels*0.20));

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = picker.getHour();
                    minute = picker.getMinute();
                }
                else{
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                selectTime.setText(hour +":"+ minute);
                dialogueShow.dismiss();
            }
        });
    }

    private void selectDate(){
        ///*****///
        //uygulama dili ingilizce olarak ayarlandıki tarihler ona göre gelsin
        Locale.setDefault(Locale.ENGLISH);
        Configuration configuration = root.getResources().getConfiguration();
        configuration.setLocale(Locale.ENGLISH);
        configuration.setLayoutDirection(Locale.ENGLISH);
        root.getContext().createConfigurationContext(configuration);
        //////////////////

        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_datepicker,null);
        //

        DatePicker picker;
        Button btnGet;;

        picker=(DatePicker)view.findViewById(R.id.datePicker);
        btnGet=view.findViewById(R.id.select);

        //seçileni date picker başlangı olarak atadık.
        String[] date = selectDate.getText().toString().split("/");
        picker.init(Integer.parseInt(date[2]), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]), null);



        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        //dialog dissmiss edilirse burası çalışır.
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                selectDate.setText(picker.getDayOfMonth()+"/"+ (picker.getMonth() + 1)+"/"+picker.getYear());
            }
        });
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();

        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.80), (int)(getResources().getDisplayMetrics().heightPixels*0.55));


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate.setText(picker.getDayOfMonth()+"/"+ (picker.getMonth() + 1)+"/"+picker.getYear());
                dialogueShow.dismiss();
            }
        });

    }

}
