package com.example.smartdisplay.ui.statistics;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ReminderAlarm.AddReminder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment {
    private View root;

    private TextView todo,inprogress,done;

    private MaterialCalendarView calendar;
    public static List<UserTask> taskList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_statistics, container, false);
        setLocaleEnglish();
        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        define();
        setInfoIntoCalendarAndCards();
        routing();


        return root;
    }

    private void define(){
        calendar = root.findViewById(R.id.calendar);

        todo = root.findViewById(R.id.todoText);
        inprogress = root.findViewById(R.id.inprogressText);
        done = root.findViewById(R.id.doneText);
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

    private void routing(){
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Bundle bundle = new Bundle();
                bundle.putInt("year", date.getYear());
                bundle.putInt("month", date.getMonth());
                bundle.putInt("day", date.getDay());

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_today, bundle);
            }
        });
    }

    private void setInfoIntoCalendarAndCards(){
        //card numbers
        int td=0,inprgrss=0,dn=0;
        //calendar view
        Calendar calendarToday = Calendar.getInstance();
        calendar.setDateSelected(calendarToday, true);

        calendar.setSelectionMode (MaterialCalendarView.SELECTION_MODE_SINGLE);
        for (UserTask task : taskList) {
            //gün olarak seçilenlerin, seçili günde olup olmadığına bakılıp eklendi.
            if (task.getIsActive() && task.getRepeatType()) {
                inprgrss++;

                calendar.addDecorator(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        return (task.getRepeatInfo())
                                .contains((day.getCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())).substring(0, 3));
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        // add foreground span
                        view.addSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(getContext(), R.color.yellow)));
                    }
                });

            }

            //tarih seçilenler kontrol edilerek eklendi.
            else if (task.getIsActive() && !task.getRepeatType()) {
                td++;

                calendar.addDecorator(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        Calendar cal1 = day.getCalendar();
                        Calendar cal2 = Calendar.getInstance();
                        cal2.set(task.getYear(), task.getMonth()-1, task.getDay());

                        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        // add foreground span
                        view.addSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(getContext(), R.color.yellow)));
                    }
                });
            }
            else{
                dn++;
            }
        }

        //
        int total = td + inprgrss +dn ;
        todo.setText(td+"/"+total);
        inprogress.setText(inprgrss+"/"+total);
        done.setText(dn+"/"+total);
    }
}