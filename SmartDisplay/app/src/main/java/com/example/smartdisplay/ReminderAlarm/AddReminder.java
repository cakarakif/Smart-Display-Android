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
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AddReminder {
    private Context context;
    private UserTask usrTask;

    public AddReminder(Context context){
        this.context=context;
    }

    public UserTask getUserTask() {
        return usrTask;
    }

    public void setUserTask(UserTask usrTask) {
        this.usrTask = usrTask;
    }

    private Calendar setCalendarWithInfo(){//task içerisindeki tarihe ve zamana göre calendar atandı
        Calendar c = Calendar.getInstance();

        if(!usrTask.getRepeatType()) {
            c.set(c.YEAR, usrTask.getYear());
            c.set(c.MONTH, usrTask.getMonth() - 1);
            c.set(c.DAY_OF_MONTH, usrTask.getDay());
        }

        c.set(c.HOUR_OF_DAY, usrTask.getHours());
        c.set(c.MINUTE, usrTask.getMinutes());
        c.set(c.SECOND, 0);

        //kontrol
        /*String timeText = "Alarm set for: ";
        timeText += DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(c.getTime());
        Toast.makeText(context,timeText+"",Toast.LENGTH_LONG).show();*/

        return c;
    }

    public void startAlarm() {
        Calendar c=setCalendarWithInfo();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //taskID'si request code olarak kullanıldı
        Intent intent = new Intent(context, AlertReceiver.class);
        //usrTask notifikasyon özelleştirmesi için gönderildi
        Gson gson = new Gson();
        String value = gson.toJson(usrTask);
        intent.putExtra("usrTask", value);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(usrTask.getId()), intent, FLAG_UPDATE_CURRENT);



        if(!usrTask.getRepeatType())//tek seferlik notification
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        else//sürekli notification(receiverde o güne dahil değilse engellendi)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(usrTask.getId()), intent, 0);

        alarmManager.cancel(pendingIntent);
        /*Toast.makeText(context,"Alarm Cancelled",Toast.LENGTH_LONG).show();*/
    }
}
