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

        //Boolean isActive,String title, String description, String goal, String repeatInfo, Boolean repeatType, String time, Boolean alertType, String videoUrl, String id
        Calendar rightNow = Calendar.getInstance();
        UserTask usrTask=new UserTask(true,"Kitap Vakti","Yarım kalan kitabini okumalisin","1","15/02/2021",false,rightNow.get(Calendar.HOUR_OF_DAY)+":"+(rightNow.get(Calendar.MINUTE)+5),true,"--","80");
        AddReminder ad=new AddReminder(root);
        ad.startAlarm(usrTask);
        //ad.onTimeSet(rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE)+1,2);


        return root;
    }
}