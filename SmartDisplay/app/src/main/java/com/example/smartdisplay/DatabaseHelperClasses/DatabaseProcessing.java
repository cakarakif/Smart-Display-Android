package com.example.smartdisplay.DatabaseHelperClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.smartdisplay.R;
import com.example.smartdisplay.ReminderAlarm.AddReminder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import android.widget.Toast;

public class DatabaseProcessing extends Fragment {
    //classı çağıran view elemanları yönetmek için alındı
    Context context;

    //genel firabase bağlantı parametreleri
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private MutableLiveData<Boolean> isCheckedCounter;//firabaseden counter alındıktan sonra işlem yapılması sağlandı-Bunun için bekleme sağlandı. Değişimden sonra gerekli classlardaki metotlar tetikledi.
    private MutableLiveData<Boolean> isSyncCalendarChecked;
    private MutableLiveData<DataSnapshot> isReadUserTasks;
    private int counter;
    private Boolean blockDouble,blockDoubleToDelete,blockDoubleToSyncCalendar;

    private  AddReminder rmndr;



    public DatabaseProcessing(View root) {//takvim için düzenlendi ve genel yapılar
        this.context=root.getContext();
        counter=1;
        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        rmndr=new AddReminder(context);
    }

    public DatabaseProcessing(Context context){//notificationslar için düzenlendi
        this.context = context;
        counter=1;
        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        rmndr=new AddReminder(context);
    }


    public void readUserCounterInfo(){//counter yapısını datebasede oluşturarak taskların sıralamasını beliledik.

             blockDouble = true;//reference'de olan değişkeni değiştirdiğimiziçin onDataChange iki kere düşmesini engelledik.


                 reference = database.getReference(user.getUid() + "/counter");

                 reference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         if (dataSnapshot.getValue() != null && blockDouble) {
                             blockDouble = false;
                             counter = Integer.parseInt(dataSnapshot.getValue().toString());
                             counter++;
                             reference.setValue(counter);
                             getCheckedCounter().postValue(true);
                         } else if (blockDouble) {
                             blockDouble = false;
                             reference.setValue(counter);
                             getCheckedCounter().postValue(true);
                         }

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                         //Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
                     }
                 });
    }


    public void saveTask(UserTask usrtask,String fromWhere){//counterdan yönlendirilen bilgiler database'e işlendi.

        if(fromWhere.equals("calendar")) {//calendardan gelenler C kodu ile kaydedildi, kolayca silinebilsin diye
            usrtask.setWhichType("C");
            reference = database.getReference(user.getUid() + "/Tasks/" + counter);//nereye kaydedileceğinin bilgisi.
        }
        usrtask.setId(counter+"");
        reference.setValue(usrtask);
        counter++;


    }

    public void updateCounterAfterSaveFinished(){//saveTask işleminden sonra burası çağrılarak en güncel counter firebasedede güncellenir.
        reference = database.getReference(user.getUid() + "/counter");
        reference.setValue(counter);
    }

    public MutableLiveData<Boolean> getCheckedCounter(){//getCheckedCounter dinlenildiği yer ile bağlantı.(observe yapısı)
        if(isCheckedCounter == null){
            isCheckedCounter = new MutableLiveData<>();
        }
        return isCheckedCounter;
    }

    public void deleteCalendarTasks(){//calendardan daha önce eklenenler temizlenir/ Yeniler eklenirken tekrarlı veri olmasın diye veya direkt calendardan çekilenler silinebilir.
        blockDoubleToDelete=true;//tasklist değiştiği için kendi kendini tetiklemesin

        reference = database.getReference(user.getUid() + "/Tasks");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("tasks", dataSnapshot.getValue() + "");

                if (dataSnapshot.getValue() != null && blockDoubleToDelete) {
                    blockDoubleToDelete=false;
                    //listeler ayrıştırılıp hangisi kullanılacaksa routingde taskliste atandı. Burada dolduruldu.
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserTask usrtasks = postSnapshot.getValue(UserTask.class);
                        if(usrtasks.getWhichType().equals("C")){
                            DatabaseReference tempReference=database.getReference(user.getUid() + "/Tasks/"+usrtasks.getId());
                            tempReference.removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setSyncCalendar(Boolean isSyncCalendarChecked){//sync calendar togglenın seçilip seçilmediğinin güncel bilgisi firabase'e yazılır
        reference = database.getReference(user.getUid() + "/SyncCalendar");
        reference.setValue(isSyncCalendarChecked);

    }

    public void getSyncCalendar(){//kullanıcının sync calendar ayarını daha önceden seçme tercihini aldık
        blockDoubleToSyncCalendar = true;//reference'de olan değişkeni değiştirdiğimiziçin onDataChange iki kere düşmesini engelledik.


        DatabaseReference references = database.getReference(user.getUid() + "/SyncCalendar");

        references.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null && blockDoubleToSyncCalendar) {
                    blockDoubleToSyncCalendar = false;
                    getisSyncCalendar().postValue((Boolean)dataSnapshot.getValue());
                } else if (blockDoubleToSyncCalendar) {
                    blockDoubleToSyncCalendar = false;
                    references.setValue(false);
                    getisSyncCalendar().postValue(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
            }
        });
    }

    public MutableLiveData<Boolean> getisSyncCalendar(){//getisSyncCalendar dinlenildiği yer ile bağlantı.(observe yapısı)
        if(isSyncCalendarChecked == null){
            isSyncCalendarChecked = new MutableLiveData<>();
        }
        return isSyncCalendarChecked;
    }

    /////////////////////////////////////////////////////////
    //Notification Parts
    public void deleteTask(String taskID){
        reference = database.getReference(user.getUid() + "/Tasks/"+
                Math.abs(Integer.parseInt(taskID)));

        reference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_LONG).show();
                        readUserTasks();//bildirimlerden'de silindi
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error deleting task!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void markCompletedTask(String taskID){//false olarak işaretle
        reference = database.getReference(user.getUid() + "/Tasks/"+
                Math.abs(Integer.parseInt(taskID))+"/isActive");

        reference.setValue(false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Completed!", Toast.LENGTH_LONG).show();
                        readUserTasks();//bildirimlerden'de silindi
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error completed task!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    ///////////////////////////////////////////////////////////////////////
    /////get user tasks

    public void readUserTasks() {
        reference = database.getReference(user.getUid() + "/Tasks");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("tasks", dataSnapshot.getValue() + "");

                if (dataSnapshot.getValue() != null) {
                    //verilerimizi aldık
                    getUserTasks().postValue(dataSnapshot);

                    //Notification ayarlaması yapıldı
                    setNotifications(dataSnapshot);
                } else
                    getUserTasks().postValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getUserTasks().postValue(null);
            }
        });
    }

    public MutableLiveData<DataSnapshot> getUserTasks(){//getUserTasks dinlenildiği yer ile bağlantı.(observe yapısı)
        if(isReadUserTasks == null){
            isReadUserTasks = new MutableLiveData<>();
        }
        return isReadUserTasks;
    }

    private void setNotifications(DataSnapshot dataSnapshot){
        int counterInfo = -1; //gelen tasklardan counter infosu alınarak otomatik mantık bildirim iptaline yönlendirildi.

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            UserTask usrtasks = postSnapshot.getValue(UserTask.class);
            if (Integer.parseInt(usrtasks.getId()) > counterInfo) {
                counterInfo = Integer.parseInt(usrtasks.getId());
            }
        }

        for (int i = 0 ; i <= counterInfo ; i++)
            rmndr.cancelAlarmDirectly(i);

        //Tüm cihazlarda aynı oturum açıldığında takip edilmesi için, yeni bir task eklendiğinde önce daha önceki bildirimler siliniyor sonra
        //güncel hali yeniden ekleniyor.

        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            UserTask usrtasks = postSnapshot.getValue(UserTask.class);

            if(usrtasks.getIsActive()) {//eğer task aktifse taskın daha önceden bildirimi varsa silinip sonra tekrar eklendi
                rmndr.setUserTask(usrtasks);
                rmndr.cancelAlarm();
                rmndr.startAlarm();
            }
        }


    }

    ////////////
    public String getUserUid(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            return user.getUid();
        }

        return "";
    }


}
