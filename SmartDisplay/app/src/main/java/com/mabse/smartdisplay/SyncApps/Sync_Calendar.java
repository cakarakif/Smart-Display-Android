package com.mabse.smartdisplay.SyncApps;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Toast;

import com.mabse.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.mabse.smartdisplay.DatabaseHelperClasses.UserTask;
import com.mabse.smartdisplay.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

public class Sync_Calendar extends Fragment{//Telefonun kendi takvimindeki taskların senkronizasyonu

    private DatabaseProcessing dtbs;
    private FragmentActivity activity;
    private View root;

    private ArrayList<UserTask> calendarList;
    public Boolean isFirstRead;//ilk okumada toggle tepki vermesin diye atama yapıldı.

    private ProgressDialog loading;

    public Sync_Calendar(View root,FragmentActivity activity) {
        this.activity=activity;//observer yapısı için gerekli olan bu yapıda alındı burayı çağıran sayfadan
        this.root=root;
        dtbs=new DatabaseProcessing(root);

        calendarList=new ArrayList<UserTask>();
        isFirstRead=false;
    }

    public void  syncCalendar(){//telefonun takviminden eventleri çeken temel metot

        //tekrarlı veriden kaçınmak için sıfırlandılar
        calendarList=new ArrayList<UserTask>();
        dtbs=new DatabaseProcessing(root);

        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE ,CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION,CalendarContract.Events.ORGANIZER};

        Calendar startTime = Calendar.getInstance();//hangi tarihten sonraki tasklar gelsin
        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();//hangi tarihten önceki tasklar gelsin
        endTime.add(Calendar.DATE, 60);//bugünden itibaren 60 günlük hatırlatmaları çeker

        //üstteki kısıtlamaları bir koşul cümlesi olarak string olarak kaydedip aşağıda kullandık
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";

        //takvimden bilgi çekme yapısı-çekme kuralları ve bilgileri tanımlanır
        Cursor cursor = root.getContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);


        //************* TEMEL MANTIK KISMI
        //Bilgiler listeye doldurulduktan sonra database'e yönlendirildi.
        //önce counter çekilmesi beklendi sonra işlem başlatıldı.


        if (cursor!=null&&cursor.getCount()>0&&cursor.moveToFirst()) {
            do {
                if(!cursor.getString(7).toString().contains("holiday")) {//tatil günlerinin eklenmesi engellendi.
                    //bilgiler listeye atandı.
                    UserTask usrtask = new UserTask(true, cursor.getString(1) + "", cursor.getString(2) == null ? "" : cursor.getString(2) + "",
                            "1", getDate(cursor.getString(3) + "", true), false, getDate(cursor.getString(3) + "", false), true, "", "C");

                    calendarList.add(usrtask);
                }

            } while ( cursor.moveToNext());

            //datebase tetiklendi counter okunduktan sonra listenCheckedCounter metodu tetiklenir
            dtbs.readUserCounterInfo();
            listenCheckedCounter();//counter bekleme yapısı içinde liste işlenmeye başlanır, tetiklendiğinde.

            //**************
        }
    }

    public String getDate(String mllscnds,Boolean whichType) {//true tarihi false ise saati döner //yardımcı metot
        long milliSeconds=Long.parseLong(mllscnds);
        SimpleDateFormat formatter;
        if(whichType)
            formatter = new SimpleDateFormat("dd/MM/yyyy");
        else
            formatter = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime()).toString();
    }


    private void listenCheckedCounter(){//counter bilgisi firabaseden çekildikten sonra burası tetiklenir

        try {
            dtbs.getCheckedCounter().observe(activity, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isCheckedCounter) {
                    //do what you want when the varriable change.

                    //ilk önce calendardan çekilenler silindi/tekrarlı veri engellendi. Sonrasında ekleme yapıldı.
                    //dtbs.setSyncCalendar(true);

                    //liste firabase işlenmeye hazır-DatabaseProcessing clasından gerekli metot uygulanır
                    for(UserTask task : calendarList){
                        dtbs.saveTask(task,"calendar");
                    }

                    dtbs.updateCounterAfterSaveFinished();

                    Toast.makeText(root.getContext(), R.string.successSync, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                }
            });
        }catch (Exception e){
        }
    }

    public void deleteCalendarTasks(){//eğer 'sync toggle' off olursa direkt silme işlemine ulaşsın
        loading = ProgressDialog.show(root.getContext(), "Please wait...", "Retrieving data ...", true);
        //dtbs.setSyncCalendar(false);
        dtbs.deleteCalendarTasks();
        //Toast.makeText(root.getContext(), R.string.successDeleted, Toast.LENGTH_SHORT).show();
    }

    public void listenSyncCalendarInfo(){//syncCalendar bilgisi firabaseden çekildikten sonra burası tetiklenir(kullanıcı daha önce aktif etmiş mi etmemiş mi)
        dtbs.getSyncCalendar();

        try {
            dtbs.getisSyncCalendar().observe(activity, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isSyncCalendarChecked) {
                    //do what you want when the varriable change.

                    //direkt sayfadaki toggle bağlanılarak checked infosu değiştirildi.
                    isFirstRead=true;
                    //ToggleButton syncCalendar=root.findViewById(R.id.syncCalendar);
                    //syncCalendar.setChecked(isSyncCalendarChecked);
                    isFirstRead=false;
                }
            });
        }catch (Exception e){
        }
    }
}
