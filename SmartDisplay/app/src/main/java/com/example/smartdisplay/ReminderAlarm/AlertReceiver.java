package com.example.smartdisplay.ReminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.google.gson.Gson;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //bildirime eklenen bilgiler çekildi
        Gson gson = new Gson();
        UserTask usrTask = gson.fromJson(intent.getStringExtra("usrTask"), UserTask.class);
        if(usrTask != null) {//actionlar için tekrar geldiğinde boş olur
            Toast.makeText(context, usrTask.getTitle() + "", Toast.LENGTH_SHORT).show();

            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(usrTask);
            notificationHelper.getManager().notify(Integer.parseInt(usrTask.getId()), nb.build());
        }

        DatabaseProcessing dtbs=new DatabaseProcessing(context);
        String action = intent.getAction();

        //Get Action1
        if(action != null && action.equals("Delete")) {
            dtbs.deleteTask(intent.getStringExtra("TaskID"));
        }

        //Get Action2
        else if(action != null && action.equals("Complete")) {
            dtbs.markCompletedTask(intent.getStringExtra("TaskID"));
        }

    }
}
