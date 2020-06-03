package com.example.smartdisplay.ui.statistics;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ReminderAlarm.AddReminder;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment {
    private View root;

    private MaterialCalendarView calendar;
    public static List<UserTask> taskList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_statistics, container, false);
        setLocaleEnglish();
        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        define();
        setDatesIntoCalendar();

        Log.i("kntrlAkif",taskList.get(0).getTitle()+""+taskList.get(1).getTitle());

        return root;
    }

    private void define(){
        calendar = root.findViewById(R.id.calendar);
    }
    private void setLocaleEnglish(){
        String languageToLoad  = "en_US"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        root.getContext().getResources().updateConfiguration(config,
                getContext().getResources().getDisplayMetrics());
    }

    private void setDatesIntoCalendar(){
        calendar.setSelectionMode (MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        Calendar calendar0 = Calendar.getInstance();
        calendar0.set(2020, 06, 11);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2020, 06, 19);

        calendar.setDateSelected(calendar0, true);
        calendar.setDateSelected(calendar1, true);
    }
}