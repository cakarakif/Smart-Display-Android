package com.example.smartdisplay.ui.add;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.smartdisplay.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {
    View root;

    CheckBox repeatLogo, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    TextView selectTime, timeText;
    RadioGroup typeRadios;
    EditText typeEdit;

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
    }

    private void routing(){
        repeatLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatLogo.isChecked()){
                    repeatLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    setAllDaysClicked();
                }else {
                    repeatLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    checkRepeatCB();
                }
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
                int selectedRadio = typeRadios.getCheckedRadioButtonId();

                if(selectedRadio == R.id.radioOne){
                    typeEdit.setVisibility(View.INVISIBLE);
                }else if(selectedRadio == R.id.radioTwo){
                    typeEdit.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void daysClicked(){
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(monday,R.drawable.daymm);
                }else {
                    setBackgroundCB(monday,R.drawable.daym);
                    repeatLogo.setChecked(false);
                    setBackgroundCB(repeatLogo,R.drawable.ic_radioempty);
                }
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tuesday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(tuesday,R.drawable.daytt);
                }else {
                    setBackgroundCB(tuesday,R.drawable.dayt);
                    repeatLogo.setChecked(false);
                    setBackgroundCB(repeatLogo,R.drawable.ic_radioempty);
                }
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wednesday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(wednesday,R.drawable.dayww);
                }else {
                    setBackgroundCB(wednesday,R.drawable.dayw);
                    checkRepeatCB();
                }
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thursday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(thursday,R.drawable.daytt);
                }else {
                    setBackgroundCB(thursday,R.drawable.dayt);
                    checkRepeatCB();
                }
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(friday,R.drawable.dayff);
                }else {
                    setBackgroundCB(friday,R.drawable.dayf);
                    checkRepeatCB();
                }
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saturday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(saturday,R.drawable.dayss);
                }else {
                    setBackgroundCB(saturday,R.drawable.days);
                    checkRepeatCB();
                }
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sunday.isChecked()){
                    checkRepeatCB();
                    setBackgroundCB(sunday,R.drawable.dayss);
                }else {
                    setBackgroundCB(sunday,R.drawable.days);
                    checkRepeatCB();
                }
            }
        });
    }

    private void setBackgroundCB(CheckBox whichone, int background){//genelleştirme için yardımcı fonksiyon
        whichone.setBackgroundResource(background);
    }

    private void checkRepeatCB(){//eğer tüm günler seçiliyse repeat checkboxı doldurulur.
        if(monday.isChecked() && tuesday.isChecked() && wednesday.isChecked()
                              && thursday.isChecked() && friday.isChecked() && saturday.isChecked() && sunday.isChecked()){
            repeatLogo.setChecked(true);
            setBackgroundCB(repeatLogo,R.drawable.ic_radiofill);
        }else{
            repeatLogo.setChecked(false);
            setBackgroundCB(repeatLogo,R.drawable.ic_radioempty);
        }
    }

    private void setAllDaysClicked(){//Repeat buttonu için işlem atandı.
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


        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(false);//buraya özel değiştirildi.
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
                //tvw.setText("Selected Date: "+ hour +":"+ minute+" "+am_pm);
                selectTime.setText(hour +":"+ minute);
                dialogueShow.dismiss();
            }
        });
    }

}
