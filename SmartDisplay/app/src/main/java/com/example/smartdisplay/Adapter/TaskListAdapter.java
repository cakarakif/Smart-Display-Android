package com.example.smartdisplay.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

public class TaskListAdapter extends BaseAdapter {

    private List<UserTask> list;
    private Context context;

    //view için tanımlamalar
    private View listDesign;
    private TextView name,date,time;
    private TextView description,goal,notification;
    private Button edit,delete,complete;

    //silme işlemi için database bağlantısı gerekli
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private ProgressDialog loading;

    //edite tıklandığında All tasks fragmentine bildirim olsun diye değişken dinlenildi.
    private MutableLiveData<Integer> editID;


    public TaskListAdapter(List<UserTask> list, Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//hangi görselde veriler gösterilecek onu bağladık ve tanımladık.

        listDesign = LayoutInflater.from(context).inflate(R.layout.design_list,viewGroup,false);

        LinearLayout mainList,moreInfo;//bunları dışarı alınca hata oluşuyor.
        mainList=listDesign.findViewById(R.id.mainList);
        moreInfo=listDesign.findViewById(R.id.moreInfo);

        //görev pasifse arka planı değişikliği
        if(!list.get(i).getIsActive()){
            mainList.setBackgroundResource(R.drawable.ic_passive_tasklist);
        }

        define();
        fillInfos(i);

        //Eğer task üzerine tıklarsa bilgi verici ekranı açılsın/kapansın
        mainList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listDesign) {
                if(moreInfo.getVisibility() == View.VISIBLE) moreInfo.setVisibility(View.GONE);
                else moreInfo.setVisibility(View.VISIBLE);
            }
        });

        routing(i);

        return listDesign;
    }

    private void define(){
        name=listDesign.findViewById(R.id.name);
        date=listDesign.findViewById(R.id.date);
        time=listDesign.findViewById(R.id.time);

        description=listDesign.findViewById(R.id.description);
        goal=listDesign.findViewById(R.id.goal);
        notification=listDesign.findViewById(R.id.notification);

        edit=listDesign.findViewById(R.id.edit);
        delete=listDesign.findViewById(R.id.delete);
        complete=listDesign.findViewById(R.id.complete);

        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user.getUid() + "/Tasks");
    }

    private void fillInfos(int i){

        if(!list.get(i).getIsActive()) complete.setBackgroundResource(R.drawable.ic_restore);

        //veriler atandı
        name.setText(list.get(i).getTitle().toUpperCase());
        date.setText(list.get(i).getRepeatInfo());
        time.setText(list.get(i).getTime());
        description.setText(list.get(i).getDescription());
        goal.setText(list.get(i).getGoal()+" day");
        notification.setText(list.get(i).getAlertType()? "Default" : "VideoURL");
    }

    private void routing(int i){
        //task silme işlemi
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areUSureDialogDelete(Integer.parseInt(list.get(i).getId()));
            }
        });

        //task silme işlemi
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routingEdit(i);
            }
        });

        //complete olarak işaretle
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areUSureTickCompleted(Integer.parseInt(list.get(i).getId()),list.get(i).getIsActive());
            }
        });
    }

    private void routingEdit(int i){//edite basınca task idsi AllTaskFragmentte || TodayTaskFragmentte dinlenilen yere gönderildi.
        geteditID().postValue(i);
    }

    public MutableLiveData<Integer> geteditID(){//editin dinlenildiği yer ile bağlantı.
        if(editID == null){
            editID = new MutableLiveData<>();
        }
        return editID;
    }


    private void areUSureDialogDelete(int id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        loading = ProgressDialog.show(context, "Please wait...", "Deleting ...", true);
                        reference = database.getReference(user.getUid() + "/Tasks/"+id);

                        //silme işlemi
                        reference.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_LONG).show();
                                        loading.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error deleting task!", Toast.LENGTH_LONG).show();
                                        loading.dismiss();
                                    }
                                });

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void areUSureTickCompleted(int id, Boolean isActive){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        loading = ProgressDialog.show(context, "Please wait...", "Saving ...", true);
                        reference = database.getReference(user.getUid() + "/Tasks/"+id+"/isActive");

                        //restore veya tamamlanmış işaretlemesi yapıldı.
                        reference.setValue(!isActive)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Successfully saved!", Toast.LENGTH_LONG).show();
                                        loading.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error changing task!", Toast.LENGTH_LONG).show();
                                        loading.dismiss();
                                    }
                                });



                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        String message=isActive? "Do you want to mark the task as completed?":"Do you want to restore the task?";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
