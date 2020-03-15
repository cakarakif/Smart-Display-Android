package com.example.smartdisplay.ui.statistics;

import android.os.Bundle;
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

import java.util.Calendar;

public class StatisticsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        AddReminder ad=new AddReminder(root.getContext());
        Calendar rightNow = Calendar.getInstance();

        /*UserTask usrTask=new UserTask(true,"Kitap Vakti1","Yarım kalan kitabini okumalisin","1","15/03/2020",false,rightNow.get(Calendar.HOUR_OF_DAY)+":"+(rightNow.get(Calendar.MINUTE)+1),true,"--","99");
        ad.setUserTask(usrTask);
        ad.startAlarm();*/
        //ad.cancelAlarm();


        AddReminder ad2=new AddReminder(root.getContext());
        UserTask usrTask2=new UserTask(true,"Kitap Vakti2","Yarım kalan kitabini okumalisin","1","Tue/Wed/Thu/Fri/Sat",true,rightNow.get(Calendar.HOUR_OF_DAY)+":"+(rightNow.get(Calendar.MINUTE)+1),true,"--","101");
        ad2.setUserTask(usrTask2);
        ad2.startAlarm();


        return root;
    }
}