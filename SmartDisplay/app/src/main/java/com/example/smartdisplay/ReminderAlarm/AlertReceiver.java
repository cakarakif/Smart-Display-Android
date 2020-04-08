package com.example.smartdisplay.ReminderAlarm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.SmartScreen.ShowVideo.show_video;
import com.example.smartdisplay.SmartScreen.SmartScreen;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseProcessing dtbs=new DatabaseProcessing(context);

        //bildirime eklenen bilgiler çekildi
        String action = intent.getAction();

        Gson gson = new Gson();
        UserTask usrTask = gson.fromJson(intent.getStringExtra("usrTask"), UserTask.class);
        //Log-out ise veya farklı kullanıcının bildirimleri varsa bu engellendi
        String UserUid=intent.getStringExtra("UserUid");

        //Main Control-Daily or Weekly
        if(usrTask != null && action== null && usrTask.getIsActive() && controlTaskType(usrTask) && UserUid.equals(dtbs.getUserUid())) {
            Toast.makeText(context, usrTask.getTitle() + "", Toast.LENGTH_SHORT).show();

            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(usrTask);
            notificationHelper.getManager().notify(Integer.parseInt(usrTask.getId()), nb.build());


            //Eğer smart-screen ekranındaysa ve video hatırlatma eklendiyse çalıştırılır.
            //ilk smart screen tetiklenir bilgiler gönderilir- diğer tarafta bilgi varsa videoyu tetikle diye yapı kuruldu.
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> activityInfo = am.getRunningTasks(1);

            if( !usrTask.getVideoUrl().equals("") && activityInfo.get(0).topActivity.getClassName().equals("com.example.smartdisplay.SmartScreen.SmartScreen") ){
                Intent myIntent = new Intent(getApplicationContext(), show_video.class);
                //String value = gson.toJson(usrTask);
                myIntent.putExtra("videoUrl", usrTask.getVideoUrl());
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            }
        }

        /**//**/


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
            AddReminder ad=new AddReminder(context);
            String after10Minute= getTimeAfter10minutes();

            //erteleme bildirimi kapatıldı sonra tekrar aşağıda kuruldu
            ad.cancelAlarmOnNotificationBar(-Math.abs(Integer.parseInt(usrTask.getId())));

            if(after10Minute != null) {
                if(Integer.parseInt(usrTask.getId()) > 0) {
                    usrTask.setId("-" + usrTask.getId());//ertelerken asıl bildirimi değiştirmesin diye ID değiştirildi ve altta tek seferlik olarak ayarlandı
                    usrTask.setRepeatType(false);
                }
                if(!usrTask.getRepeatType())//kontrol & snooze edilenler tek seferlik olarak ayarlandı
                    usrTask.setRepeatInfo(after10Minute.substring(0,10));

                usrTask.setTime(after10Minute.substring(after10Minute.length()-5));
            }

            ad.setUserTask(usrTask);
            ad.startAlarm();
            Toast.makeText(context,"Snoozed for 10 minutes",Toast.LENGTH_LONG).show();
        }



        if(action != null && (action.equals("Complete") || action.equals("Delete"))){
            int taskID=Math.abs(Integer.parseInt(intent.getStringExtra("TaskID")));
            AddReminder ad=new AddReminder(context);
            ad.cancelAlarmDirectly(taskID);
            ad.cancelAlarmDirectly(-taskID);//Ertelemesi varsa o da iptal edildi

            //bu kod bildirimi sistemden komple siler kaldırır-hemde notification pencesini kapatır

            ad.cancelAlarmOnNotificationBar(taskID);
            ad.cancelAlarmOnNotificationBar(-taskID);
        }

    }

    public boolean controlTaskType(UserTask usrTask){//Haftalık tasklar hergün ayarlı ama tetiklenip tetiklenmeyeceğine burada karar verilir.

        Calendar rightNow = Calendar.getInstance();

        //alarmları resetlerken eskiler tekrar tetiklenmesi engellendi
        if(!usrTask.getRepeatType()) {
            try {
                String first = usrTask.getRepeatInfo() + " " + usrTask.getTime();
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatter.format(rightNow.getTime());
                if(formatter.parse(first).before(formatter.parse(formatter.format(rightNow.getTime()))))
                    return false;
            } catch (Exception e) {

            }
        }

        //bu kod sadece zaman olarak kıyaslıyor üstte hem tarih hem zaman içeriyor.
        if(usrTask.getHours() < rightNow.get(Calendar.HOUR_OF_DAY) ||
                (usrTask.getHours() == rightNow.get(Calendar.HOUR_OF_DAY) && usrTask.getMinutes() <  rightNow.get(Calendar.MINUTE))){
            return false;
        }

        if(!usrTask.getRepeatType() && usrTask.getIsActive()) {
            return true;
        }
        else if(usrTask.getRepeatType() && usrTask.getIsActive()){
            Locale.setDefault(Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String todayShort=(calendar.getDisplayName(calendar.DAY_OF_WEEK,calendar.SHORT, Locale.getDefault())).substring(0,3).toUpperCase();

            return (usrTask.getRepeatInfo().toUpperCase().indexOf(todayShort) == -1 ? false: true);
        }
        return false;
    }

    public static String getTimeAfter10minutes() {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            // Get calendar set to current date and time with Singapore time zone
            Calendar calendar = Calendar.getInstance();
            //calendar.setTime(format.parse(currentDate));

            //Set calendar after 10 minutes
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
