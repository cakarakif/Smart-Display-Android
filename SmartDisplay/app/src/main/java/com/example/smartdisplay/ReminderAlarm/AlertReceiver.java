package com.example.smartdisplay.ReminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //bildirime eklenen bilgiler çekildi
        String message = intent.getStringExtra("toastMessage");
        if(!message.equals(""))
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();



        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
    }
}
