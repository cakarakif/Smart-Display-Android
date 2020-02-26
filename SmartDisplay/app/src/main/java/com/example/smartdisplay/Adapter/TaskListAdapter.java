package com.example.smartdisplay.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;

import org.w3c.dom.Text;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {

    private List<UserTask> list;
    private Context context;

    //view için tanımlamalar
    View listDesign;
    TextView name,date,time;
    TextView description,goal,notification;
    Button edit,delete;


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
        //if(!list.get(i).getIsActive()){
        //    mainList.setBackgroundResource(R.drawable.ic_passive_tasklist);
        //}

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

        //task silme işlemi
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("geldii",list.get(i).getTitle().toUpperCase());
            }
        });





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
    }

    private void fillInfos(int i){
        //veriler atandı
        name.setText(list.get(i).getTitle().toUpperCase());
        date.setText(list.get(i).getRepeatInfo());
        time.setText(list.get(i).getTime());
        description.setText(list.get(i).getDescription());
        goal.setText(list.get(i).getGoal()+" day");
        notification.setText(list.get(i).getAlertType()? "Default" : "VideoURL");
    }
}
