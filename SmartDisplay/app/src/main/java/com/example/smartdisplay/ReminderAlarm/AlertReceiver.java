package com.example.smartdisplay.ReminderAlarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //bildirime eklenen bilgiler çekildi
        String action = intent.getAction();

        Gson gson = new Gson();
        UserTask usrTask = gson.fromJson(intent.getStringExtra("usrTask"), UserTask.class);
        if(usrTask != null && action== null) {//actionlar için tekrar geldiğinde boş olur
            Toast.makeText(context, usrTask.getTitle() + "", Toast.LENGTH_SHORT).show();

            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(usrTask);
            notificationHelper.getManager().notify(Integer.parseInt(usrTask.getId()), nb.build());
        }

        DatabaseProcessing dtbs=new DatabaseProcessing(context);

        //Get Action1
        if(action != null && action.equals("Delete")) {
            dtbs.deleteTask(intent.getStringExtra("TaskID"));
        }

        //Get Action2
        else if(action != null && action.equals("Complete")) {
            dtbs.markCompletedTask(intent.getStringExtra("TaskID"));
        }

        //Get Action3
        else if(action != null && action.equals("Snooze")) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(Integer.parseInt(usrTask.getId()));


            AddReminder ad=new AddReminder(context);
            String after10Minute= getTimeAfter10minutes();
            if(after10Minute != null) {
                usrTask.setRepeatInfo(after10Minute.substring(0,10));
                usrTask.setTime(after10Minute.substring(after10Minute.length()-5));
            }
            ad.setUserTask(usrTask);
            ad.startAlarm();
            Toast.makeText(context,"Snoozed for 10 minutes",Toast.LENGTH_LONG).show();
        }

    }

    public static String getTimeAfter10minutes() {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            // Get calendar set to current date and time with Singapore time zone
            Calendar calendar = Calendar.getInstance();
            //calendar.setTime(format.parse(currentDate));

            //Set calendar before 10 minutes
            calendar.add(Calendar.MINUTE, 10);
            //Formatter
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
