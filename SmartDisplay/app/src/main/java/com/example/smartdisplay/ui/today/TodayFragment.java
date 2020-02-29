package com.example.smartdisplay.ui.today;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartdisplay.Adapter.TaskListAdapter;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodayFragment extends Fragment {
    private View root;

    private ListView taskListView;
    private List<UserTask> taskList, filteredList;
    private TaskListAdapter listAdapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private ProgressDialog loading;

    private LinearLayout one,two,three,four,five,six,seven;
    private TextView oneName,oneDate, twoName,twoDate, threeName,threeDate, fourName,fourDate, fiveName,fiveDate, sixName,sixDate,sevenName,sevenDate;
    private Calendar cal;
    private Button filterMenu;
    private TextView todayTitle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_today, container, false);

        cal = Calendar.getInstance();//ilk başta çekilir sadece

        define();
        routing();

        readUserTasks();
        //temel calendar yapısı oluşturulup gerekli yerlerde çağrıldı
        dateCalendar(0);



        return root;
    }

    private void define(){
        taskListView =root.findViewById(R.id.taskListView);

        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user.getUid() + "/Tasks");

        one = root.findViewById(R.id.one);
        two = root.findViewById(R.id.two);
        three = root.findViewById(R.id.three);
        four = root.findViewById(R.id.four);
        five = root.findViewById(R.id.five);
        six = root.findViewById(R.id.six);
        seven = root.findViewById(R.id.seven);

        oneName = root.findViewById(R.id.oneName);
        oneDate = root.findViewById(R.id.oneDate);
        twoName = root.findViewById(R.id.twoName);
        twoDate = root.findViewById(R.id.twoDate);
        threeName = root.findViewById(R.id.threeName);
        threeDate = root.findViewById(R.id.threeDate);
        fourName = root.findViewById(R.id.fourName);
        fourDate = root.findViewById(R.id.fourDate);
        fiveName = root.findViewById(R.id.fiveName);
        fiveDate = root.findViewById(R.id.fiveDate);
        sixName = root.findViewById(R.id.sixName);
        sixDate = root.findViewById(R.id.sixDate);
        sevenName = root.findViewById(R.id.sevenName);
        sevenDate = root.findViewById(R.id.sevenDate);

        filterMenu = root.findViewById(R.id.filterMenu);

        todayTitle= root.findViewById(R.id.todayTitle);
    }

    private void routing(){

        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //eklenecek
            }
        });

        /////////////////////////
        //takvim seçme işlemleri
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(-3);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(-2);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(-1);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(0);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(1);
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(2);
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCalendar(3);
            }
        });
    }

    private void dateCalendar(int numberMov){
        //seçilen tarihe göre ayarlama ve atamalar yapıldı.
        //number kaç ileri-geri gittiği alınarak sabit mantık işletildi.

        Locale.setDefault(Locale.ENGLISH);

        cal.add(cal.DATE, numberMov);
        fourDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        fourName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        //Eğer bugünün günü seçilmediyse üste o tarih yazılır.
        if(cal.get(cal.DAY_OF_MONTH) == Calendar.getInstance().get(cal.DAY_OF_MONTH) &&
                (cal.get(cal.MONTH)+1) == (Calendar.getInstance().get(cal.MONTH)+1) &&
                cal.get(cal.YEAR) == Calendar.getInstance().get(cal.YEAR)){

            todayTitle.setText(getString(R.string.today));
        }else{
            todayTitle.setText((cal.getDisplayName(cal.MONTH,cal.SHORT, Locale.getDefault()))+" "+cal.get(cal.DAY_OF_MONTH));
        }

        //ileri üç gün
        cal.add(cal.DATE, 1);
        fiveDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        fiveName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        cal.add(cal.DATE, 1);
        sixDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        sixName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        cal.add(cal.DATE, 1);
        sevenDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        sevenName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        //geri üç gün
        cal.add(cal.DATE, -4);
        threeDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        threeName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        cal.add(cal.DATE, -1);
        twoDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        twoName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        cal.add(cal.DATE, -1);
        oneDate.setText(cal.get(cal.DAY_OF_MONTH)+"");
        oneName.setText((cal.getDisplayName(cal.DAY_OF_WEEK,cal.SHORT, Locale.getDefault())).substring(0,3).toUpperCase());

        //default almak için dördüncüye çekildi
        cal.add(cal.DATE, 3);

        //her seferinde liste üzerinden yeniden filtreleme yapıldı
        filterList();
    }

    private void readUserTasks() {
        loading = ProgressDialog.show(getContext(), "Please wait...", "Retrieving data ...", true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("tasks", dataSnapshot.getValue() + "");

                if (dataSnapshot.getValue() != null) {
                    //verilerimizi aldık
                    taskList = new ArrayList<>();

                    //tüm tasklar alındı.
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserTask usrtasks = postSnapshot.getValue(UserTask.class);
                        taskList.add(usrtasks);
                    }

                    filterList();

                } else {
                }
                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        });
    }

    private void filterList() {//listeyi seçili tarihe göre filtreleme
        if (taskList != null) {
            filteredList = new ArrayList<>();

            for (UserTask list : taskList) {

                //gün olarak seçilenlerin, seçili günde olup olmadığına bakılıp eklendi.
                if (list.getIsActive() && list.getRepeatType() && (list.getRepeatInfo())
                        .contains((cal.getDisplayName(cal.DAY_OF_WEEK, cal.SHORT, Locale.getDefault())).substring(0, 3))) {
                    filteredList.add(list);
                }

                //tarih seçilenler kontrol edilerek eklendi.
                if (list.getIsActive() && !list.getRepeatType()) {
                    String[] dates = list.getRepeatInfo().split("/");

                    if (cal.get(cal.DAY_OF_MONTH) == Integer.parseInt(dates[0]) &&
                            (cal.get(cal.MONTH) + 1) == Integer.parseInt(dates[1]) &&
                            cal.get(cal.YEAR) == Integer.parseInt(dates[2])) {
                        filteredList.add(list);
                    }
                }
            }

            //liste başlatıldı.
            startListView();
        }
    }

    private void startListView(){

        //list'in nasıl görüneceğinin adapterı
        listAdapter = new TaskListAdapter(filteredList, getContext());

        //bağlama işlemi yaptık
        taskListView.setAdapter(listAdapter);

        //edite tıklanma dinlemesi yapıldı
        listenEditClicked();
    }

    private void listenEditClicked(){//edit için add sayfasına bilgiler ile birlikte yönlendirme yapıldı.
        try {
            listAdapter.geteditID().observe(getActivity(), new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer editId) {
                    //do what you want when the varriable change.

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selectedTask", filteredList.get(editId));

                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_add, bundle);
                }
            });
        }catch (Exception e){

        }
    }

}