package com.mabse.smartdisplay.ReminderAlarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mabse.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.mabse.smartdisplay.DatabaseHelperClasses.UserTask;
import com.mabse.smartdisplay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AddReminder {
    private Context context;
    private UserTask usrTask;

    private FirebaseUser user;
    private FirebaseAuth auth;

    private AlarmManager alarmManager;
    private Intent intent;

    public AddReminder(Context context){
        this.context=context;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, AlertReceiver.class);
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


        //taskID'si request code olarak kullanıldı
        Intent intent = new Intent(context, AlertReceiver.class);
        //usrTask notifikasyon özelleştirmesi için gönderildi
        Gson gson = new Gson();
        String value = gson.toJson(usrTask);
        intent.putExtra("usrTask", value);

        //receiverda diğer kullanıcıların ekledikleri bildirimler varsa onlar engellendi
        DatabaseProcessing dtbs=new DatabaseProcessing(context);
        intent.putExtra("UserUid", dtbs.getUserUid());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(usrTask.getId()), intent, FLAG_UPDATE_CURRENT);



        if(!usrTask.getRepeatType())//tek seferlik notification
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        else//sürekli notification(receiverde o güne dahil değilse engellendi)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    public void cancelAlarm() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(usrTask.getId()), intent, 0);

        alarmManager.cancel(pendingIntent);
        /*Toast.makeText(context,"Alarm Cancelled",Toast.LENGTH_LONG).show();*/
    }

    public void cancelAlarmDirectly(int taskID) {//Direkt olarak silmeler için id gönderimi ile ek seçenek sağlandı(Aynı işlem)

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskID, intent, 0);

        alarmManager.cancel(pendingIntent);
        /*Toast.makeText(context,"Alarm Cancelled",Toast.LENGTH_LONG).show();*/
    }

    public  void cancelAlarmOnNotificationBar(int taskID){//Notification barından silmek için burası kullanılır.
        //bu kod bildirimi sistemden komple siler kaldırır-hemde notification pencesini kapatır
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(taskID);
    }

    /**//**//**//**//**//**//**//**//**//**/
    /**//**//**//**//**/
    public boolean isNotificationsActiveOnDevice(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                isChannelBlocked("TaskNotify")) {
            Toast.makeText(context, context.getString(R.string.isNotificationsActive), Toast.LENGTH_LONG).show();
            openChannelSettings("TaskNotify");
            return false;
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Toast.makeText(context, context.getString(R.string.isNotificationsActive), Toast.LENGTH_LONG).show();
            openNotificationSettings();
            return false;
        }

        return true;

    }

    private void openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    @RequiresApi(26)
    private boolean isChannelBlocked(String channelId) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        NotificationChannel channel = manager.getNotificationChannel(channelId);

        return channel != null &&
                channel.getImportance() == NotificationManager.IMPORTANCE_NONE;
    }

    @RequiresApi(26)
    private void openChannelSettings(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        context.startActivity(intent);
    }

    /**//**//**//**//**/
    /**//**//**//**//**//**//**//**//**//**/
}
