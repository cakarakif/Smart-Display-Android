package com.example.smartdisplay.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;

import org.w3c.dom.Text;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {

    private List<UserTask> list;
    private Context context;

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

        View listDesign = LayoutInflater.from(context).inflate(R.layout.design_list,viewGroup,false);
        TextView name=listDesign.findViewById(R.id.name);
        TextView date=listDesign.findViewById(R.id.date);
        TextView time=listDesign.findViewById(R.id.time);

        name.setText(list.get(i).getTitle().toUpperCase());
        date.setText(list.get(i).getRepeatInfo());
        time.setText(list.get(i).getTime());


        return listDesign;
    }
}
