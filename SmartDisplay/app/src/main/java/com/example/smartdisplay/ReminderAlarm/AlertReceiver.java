package com.example.smartdisplay.ReminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.google.gson.Gson;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //bildirime eklenen bilgiler çekildi
        String message = intent.getStringExtra("toastMessage");
        if(message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        //
        Gson gson = new Gson();
        UserTask usrTask = gson.fromJson(intent.getStringExtra("usrTask"), UserTask.class);
        Toast.makeText(context, usrTask.getTitle()+"", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(usrTask);
        notificationHelper.getManager().notify(Integer.parseInt(usrTask.getId()), nb.build());
    }
}
