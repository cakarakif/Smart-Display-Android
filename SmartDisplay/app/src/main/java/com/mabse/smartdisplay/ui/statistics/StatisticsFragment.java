package com.mabse.smartdisplay.ui.statistics;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mabse.smartdisplay.DatabaseHelperClasses.UserTask;
import com.mabse.smartdisplay.R;
import com.mabse.smartdisplay.ReminderAlarm.AddReminder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StatisticsFragment extends Fragment {
    private View root;
    //private AdView mAdView;

    private TextView todo,inprogress,done,comment;

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
        setComment();


        return root;
    }

    private void define(){
        calendar = root.findViewById(R.id.calendar);

        todo = root.findViewById(R.id.todoText);
        inprogress = root.findViewById(R.id.inprogressText);
        done = root.findViewById(R.id.doneText);

        comment = root.findViewById(R.id.comment);

        /*mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
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

    private void setComment(){
        String[] sentences = new String[] {
                "'Imagination is more important than knowledge.' \n Albert, Einstein ",
                "'It does not matter how slowly you go as long as you do not stop.' \n Confucius ",
                "'I never dreamed about success, I worked for it.' \n Estée Lauder",
                "'Begin to be now what you will be hereafter.' \n William James",
                "'You can’t give up! When you give up, you're like everybody else!' \n Chris Evert",
                "'There is no easy way from the earth to the stars.' \n Seneca",
                "'It is not in the stars to hold our destiny but in ourselves.' \n William Shakespeare",
                "'One is not born a genius, one becomes a genius.' \n Simone de Beauvoir",
                "'Change the world by being yourself.' \n Amy Poehler",
                "'Every moment is a fresh beginning.' \n T.S Eliot",
                "'Simplicity is the ultimate sophistication.' \n Leonardo da Vinci",
                "'Whatever you do, do it well.' \n Walt Disney",
                "'All limitations are self-imposed.' \n Oliver Wendell Holmes",
                "'Reality is wrong, dreams are for real.' \n Tupac",
                "'Strive for greatness.' \n Lebron James",
                "'The meaning of life is to give life meaning.' \n Ken Hudgins"};

        Random r=new Random(); //random sınıfı
        int randomNumber=r.nextInt(sentences.length);

        comment.setText(sentences[randomNumber]);
    }
}