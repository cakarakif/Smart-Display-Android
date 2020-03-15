package com.example.smartdisplay.ReminderAlarm;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ui.today.TodayFragment;
import com.google.gson.Gson;

import androidx.core.app.NotificationCompat;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Task Notifications";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(UserTask usrTask) {

        //bildirime tıklandığında uygulama açıldı
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                Integer.parseInt(usrTask.getId()), activityIntent, 0);


        //Set Action1
        Intent actionOne = new Intent(this, AlertReceiver.class);
        actionOne.setAction("Delete");
        actionOne.putExtra("TaskID",usrTask.getId());
        PendingIntent actionOneIntent = PendingIntent.getBroadcast(this,
                Integer.parseInt(usrTask.getId()), actionOne, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set Action2
        Intent actionTwo = new Intent(this, AlertReceiver.class);
        actionTwo.setAction("Complete");
        actionTwo.putExtra("TaskID",usrTask.getId());
        PendingIntent actionTwoIntent = PendingIntent.getBroadcast(this,
                Integer.parseInt(usrTask.getId()), actionTwo, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set Action3
        Intent actionThree = new Intent(this, AlertReceiver.class);
        actionThree.setAction("Snooze");
        actionThree.putExtra("TaskID",usrTask.getId());
        Gson gson = new Gson();
        String value = gson.toJson(usrTask);
        actionThree.putExtra("usrTask", value);
        PendingIntent actionThreeIntent = PendingIntent.getBroadcast(this,
                Integer.parseInt(usrTask.getId()), actionThree, PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(usrTask.getTitle())
                .setContentText(usrTask.getDescription().equals("") ? "Time to check tasks!":usrTask.getDescription())
                .setSmallIcon(R.drawable.ic_goal_yellow)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(getColor(R.color.yellow))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Snooze", actionThreeIntent)
                .addAction(R.mipmap.ic_launcher, "Complete", actionTwoIntent)
                .addAction(R.drawable.calendar, "Delete", actionOneIntent);
    }
}
