package com.mabse.smartdisplay.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mabse.smartdisplay.DatabaseHelperClasses.UserTask;
import com.mabse.smartdisplay.R;
import com.mabse.smartdisplay.ReminderAlarm.AddReminder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

public class SmartScreenTaskListAdapter extends BaseAdapter {


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


    public SmartScreenTaskListAdapter(List<UserTask> list, Context context){
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

        listDesign = LayoutInflater.from(context).inflate(R.layout.smart_screen_design_list,viewGroup,false);

        LinearLayout mainList,moreInfo;//bunları dışarı alınca hata oluşuyor.
        mainList=listDesign.findViewById(R.id.mainList);
        moreInfo=listDesign.findViewById(R.id.moreInfo);

        define();
        fillInfos(i);

        return listDesign;
    }

    private void define(){
        name=listDesign.findViewById(R.id.name);
        time=listDesign.findViewById(R.id.time);
    }

    private void fillInfos(int i){
        //veriler atandı
        name.setText(list.get(i).getTitle().toUpperCase());
        time.setText(list.get(i).getTime());
    }
}
