package com.example.smartdisplay.ui.today;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartdisplay.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodayFragment extends Fragment {
    private View root;

    private LinearLayout one,two,three,four,five,six,seven;
    private TextView oneName,oneDate, twoName,twoDate, threeName,threeDate, fourName,fourDate, fiveName,fiveDate, sixName,sixDate,sevenName,sevenDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_today, container, false);


        define();
        routing();

        int numberMov=2;
        dateCalendar(numberMov);


        return root;
    }

    private void define(){
        one = root.findViewById(R.id.one);
        two = root.findViewById(R.id.two);
        three = root.findViewById(R.id.three);
        four = root.findViewById(R.id.four);
        five = root.findViewById(R.id.five);
        six = root.findViewById(R.id.six);
        seven = root.findViewById(R.id.seven);

        oneName = root.findViewById(R.id.oneName);
        oneDate = root.findViewById(R.id.oneDate);
        twoName = root.findViewById(R.id.twoName);
        twoDate = root.findViewById(R.id.twoDate);
        threeName = root.findViewById(R.id.threeName);
        threeDate = root.findViewById(R.id.threeDate);
        fourName = root.findViewById(R.id.fourName);
        fourDate = root.findViewById(R.id.fourDate);
        fiveName = root.findViewById(R.id.fiveName);
        fiveDate = root.findViewById(R.id.fiveDate);
        sixName = root.findViewById(R.id.sixName);
        sixDate = root.findViewById(R.id.sixDate);
        sevenName = root.findViewById(R.id.sevenName);
        sevenDate = root.findViewById(R.id.sevenDate);
    }

    private void routing(){

    }

    private void dateCalendar(int numberMov){
        //seçilen tarihe göre ayarlama ve atamalar yapıldı.
        //number kaç ileri-geri gittiği alınarak sabit mantık işletildi.

        Locale.setDefault(Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();

        cal.add(cal.DATE, numberMov);
        fourDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        fourName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        //ileri üç gün
        cal.add(Calendar.DATE, 1);
        fiveDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        fiveName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        cal.add(Calendar.DATE, 1);
        sixDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        sixName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        cal.add(Calendar.DATE, 1);
        sevenDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        sevenName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        //geri üç gün
        cal.add(Calendar.DATE, -4);
        threeDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        threeName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        cal.add(Calendar.DATE, -1);
        twoDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        twoName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());

        cal.add(Calendar.DATE, -1);
        oneDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        oneName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,2).toUpperCase());
    }

}