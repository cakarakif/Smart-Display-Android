package com.example.smartdisplay.ReminderAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminder {
    private View root;
    private UserTask usrTask;

    public AddReminder(View root){
        this.root=root;
    }

    public UserTask getUserTask() {
        return usrTask;
    }

    public void setUserTask(UserTask usrTask) {
        this.usrTask = usrTask;
    }

    private Calendar setCalendarWithInfo(){//task içerisindeki tarihe ve zamana göre calendar atandı
        Calendar c = Calendar.getInstance();

        c.set(c.YEAR,usrTask.getYear());
        c.set(c.MONTH,usrTask.getMonth()-1);
        c.set(c.DAY_OF_MONTH,usrTask.getDay());

        c.set(c.HOUR_OF_DAY, usrTask.getHours());
        c.set(c.MINUTE, usrTask.getMinutes());
        c.set(c.SECOND, 0);

        //kontrol-Silinebilir en son
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(c.getTime());
        Toast.makeText(root.getContext(),timeText+"",Toast.LENGTH_LONG).show();

        return c;
    }

    public void startAlarm() {
        Calendar c=setCalendarWithInfo(); // ilk olarak takvim ayarlandı

        AlarmManager alarmManager = (AlarmManager) root.getContext().getSystemService(Context.ALARM_SERVICE);

        AlertReceiver artRcvr=new AlertReceiver();
        artRcvr.setUsrTask(usrTask);


        //taskID'si request code olarak kullanıldı

        Intent intent = new Intent(root.getContext(), artRcvr.getClass());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(root.getContext(), Integer.parseInt(usrTask.getId()), intent, 0);


        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) root.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(root.getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(root.getContext(), Integer.parseInt(usrTask.getId()), intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(root.getContext(),"Alarm Cancelled",Toast.LENGTH_LONG).show();
    }
}
