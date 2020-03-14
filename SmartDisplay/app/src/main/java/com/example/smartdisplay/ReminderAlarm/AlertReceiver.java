package com.example.smartdisplay.ReminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    private   UserTask usrTask;

    public UserTask getUsrTask() {
        return usrTask;
    }

    public void setUsrTask(UserTask usrTask) {
        this.usrTask = usrTask;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //bildirime eklenen bilgiler çekildi
        String message = intent.getStringExtra("toastMessage");
        if(message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();



        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(usrTask);
        notificationHelper.getManager().notify(1, nb.build());
    }
}
