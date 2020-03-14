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

import com.example.smartdisplay.R;

import java.text.DateFormat;
import java.util.Calendar;

public class AddReminder {
    private View root;

    public AddReminder(View root){
        this.root=root;
    }

    public void onTimeSet(int hourOfDay, int minute,int code) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c,code);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        Toast.makeText(root.getContext(),timeText+"",Toast.LENGTH_LONG).show();
    }

    private void startAlarm(Calendar c,int code) {
        AlarmManager alarmManager = (AlarmManager) root.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(root.getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(root.getContext(), code, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) root.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(root.getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(root.getContext(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(root.getContext(),"Alarm Cancelled",Toast.LENGTH_LONG).show();
    }
}
