package com.example.smartdisplay.SyncApps;

import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Sync_Calendar {

    public Sync_Calendar() {
    }

    public static void  syncCalendar(View root){
        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION };

        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";
        Cursor cursor = root.getContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);

        List<String> events = new ArrayList<>();
        if (cursor!=null&&cursor.getCount()>0&&cursor.moveToFirst()) {
            do {
                Log.i("kntrl1234",cursor.getString(0)+"((");
                Log.i("kntrl1234",cursor.getString(1)+"))");
                Log.i("kntrl1234",cursor.getString(2)+"??");
                Log.i("kntrl1234",getDate(Long.parseLong(cursor.getString(3)))+"--");
                Log.i("kntrl1234",getDate(Long.parseLong(cursor.getString(4)))+"--");
                Log.i("kntrl1234",cursor.getString(5)+"**");
                Log.i("kntrl1234",cursor.getString(6)+"//");
                Log.i("kntrl1234","-------------");
                events.add(cursor.getString(1));
            } while ( cursor.moveToNext());
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
