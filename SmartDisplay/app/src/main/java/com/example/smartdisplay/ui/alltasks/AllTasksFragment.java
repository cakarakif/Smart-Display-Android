package com.example.smartdisplay.ui.alltasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private View root;

    private List<UserTask> taskList, todoList, doneList;
    private List<UserTask> tempSearchtaskList;
    private TaskListAdapter listAdapter;
    private ListView listView;
    private Button searchLogo,closeSearch,intrvlCal;
    private RadioButton todo,done;
    private RadioGroup tabs;

    private ProgressDialog loading;

    private EditText search;
    private Button filterMenu;
    private PopupMenu popup;

    private DatabaseProcessing dtbs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_alltasks, container, false);

        define();
        readUserTasks();
        routing();

        return root;
    }

    private void define() {
        dtbs=new DatabaseProcessing(root);

        listView = root.findViewById(R.id.taskListView);

        search=root.findViewById(R.id.search);
        searchLogo=root.findViewById(R.id.searchLogo);

        todo=root.findViewById(R.id.todo);
        done=root.findViewById(R.id.done);
        closeSearch=root.findViewById(R.id.closeSearch);
        tabs=root.findViewById(R.id.tabs);

        filterMenu = root.findViewById(R.id.filterMenu);
    }

    private void readUserTasks(){//task bilgisi DatabaseProcessingden sonra burası tetiklenir

        dtbs.readUserTasks();//okuma için tetiklendi


        loading = ProgressDialog.show(getContext(), "Please wait...", "Retrieving data ...", true);
        try {//tetiklenen işlem postvalue olunca burası tetiklenir
            dtbs.getUserTasks().observe(getActivity(), new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        //verilerimizi aldık
                        taskList = new ArrayList<>();
                        todoList = new ArrayList<>();
                        doneList = new ArrayList<>();

                        //listeler ayrıştırılıp hangisi kullanılacaksa routingde taskliste atandı. Burada dolduruldu.
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            UserTask usrtasks = postSnapshot.getValue(UserTask.class);
                            if(usrtasks.getIsActive())
                                todoList.add(usrtasks);
                            else
                                doneList.add(usrtasks);
                        }

                        //herhangi bir değişiklikte databaseden gelen veri için yönlendirme yapıldı.
                        if(todo.isChecked() || (!todo.isChecked() && !done.isChecked()))
                            performToDoView();
                        else
                            performDoneView();
                    }
                    loading.dismiss();
                }
            });
        }catch (Exception e){
        }
    }


    private void routing(){
        //tab'a göre liste ataması yapıldı.
        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.todo:
                        performToDoView();
                        break;
                    case R.id.done:
                        performDoneView();
                        break;
                }
            }
        });

        //Filtreleme popupı başlatıldı ve yönledirildi.
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup = new PopupMenu(root.getContext(), filterMenu);
                popup.inflate(R.menu.today_option_menu);
                startPopupMenu();
                popup.show();

            }
        });
    }

    private void startListView(){
        tempSearchtaskList = new ArrayList<>(taskList);//search için yedeklendi
        searchText();//search aktif edildi.

        //list'in nasıl görüneceğinin adapterı
        listAdapter = new TaskListAdapter(taskList, getContext());

        //bağlama işlemi yaptık
        listView.setAdapter(listAdapter);

        //edite tıklanma dinlemesi yapıldı
        listenEditClicked();
    }

    private void performToDoView(){
        //To-do seçildiğindeki atamalar
        taskList=new ArrayList<>(todoList);
        //default olarak date ve time'a göre sıralandı.
        Collections.sort(taskList, (p1, p2) -> (p1.getHours() - p2.getHours())*60+p1.getMinutes() - p2.getMinutes());
        Collections.sort(taskList, (p1, p2) -> (p1.getYear() - p2.getYear()) * 365 + (p1.getMonth() - p2.getMonth()) * 30 + p1.getDay() - p2.getDay());
        startListView();
        todo.setBackgroundResource(R.drawable.ic_tab_fill);
        done.setBackgroundResource(R.drawable.ic_tab_empty);
    }

    private void performDoneView(){
        //Done seçildiğindeki atamalar
        taskList=new ArrayList<>(doneList);
        //default olarak date ve time'a göre sıralandı.
        Collections.sort(taskList, (p1, p2) -> (p1.getHours() - p2.getHours())*60+p1.getMinutes() - p2.getMinutes());
        Collections.sort(taskList, (p1, p2) -> (p1.getYear() - p2.getYear()) * 365 + (p1.getMonth() - p2.getMonth()) * 30 + p1.getDay() - p2.getDay());
        startListView();
        todo.setBackgroundResource(R.drawable.ic_tab_empty);
        done.setBackgroundResource(R.drawable.ic_tab_fill);
    }

    private void listenEditClicked(){//edit için add sayfasına bilgiler ile birlikte yönlendirme yapıldı.
        try {
            listAdapter.geteditID().observe(getActivity(), new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer editId) {
                    //do what you want when the varriable change.

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selectedTask", taskList.get(editId));

                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_add, bundle);
                }
            });
        }catch (Exception e){

        }
    }

    private void searchText(){
        searchLogo.setOnClickListener(new View.OnClickListener() {//search mantığı tasarlandı
            @Override
            public void onClick(View view) {

                if(search.getVisibility() == View.INVISIBLE) {
                    search.setVisibility(View.VISIBLE);
                    closeSearch.setVisibility(View.VISIBLE);
                    todo.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    search.setText("");

                    //klavye açıldı ve search edittexine tıklandı
                    search.requestFocus();
                    InputMethodManager imm = (InputMethodManager) root.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    search.setVisibility(View.INVISIBLE);
                    closeSearch.setVisibility(View.INVISIBLE);
                    todo.setVisibility(View.VISIBLE);
                    done.setVisibility(View.VISIBLE);
                    search.setText("");
                    //klavye kapatıldı.
                    InputMethodManager imm = (InputMethodManager) root.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLogo.performClick();
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                taskList=new ArrayList<>(tempSearchtaskList);;
                taskList.removeIf(p -> !p.getTitle().toUpperCase().contains(charSequence.toString().toUpperCase()));

                //list'in nasıl görüneceğinin adapterı
                listAdapter = new TaskListAdapter(taskList, getContext());

                //bağlama işlemi yaptık
                listView.setAdapter(listAdapter);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void startPopupMenu(){//istenilen filtrelemeler liste üzerinde yapıldı.
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sortAz:
                        Collections.sort(taskList, (p1, p2) -> p1.getTitle().toUpperCase().compareTo(p2.getTitle().toUpperCase()));
                        startListView();
                        return true;
                    case R.id.sortZa:
                        Collections.sort(taskList, (p1, p2) -> p2.getTitle().toUpperCase().compareTo(p1.getTitle().toUpperCase()));
                        startListView();
                        return true;
                    case R.id.sortCf:
                        Collections.sort(taskList, (p1, p2) -> (p1.getHours() - p2.getHours())*60+p1.getMinutes() - p2.getMinutes());
                        Collections.sort(taskList, (p1, p2) -> (p1.getYear() - p2.getYear()) * 365 + (p1.getMonth() - p2.getMonth()) * 30 + p1.getDay() - p2.getDay());
                        startListView();
                        return true;
                    case R.id.sortFc:
                        Collections.sort(taskList, (p1, p2) -> (p2.getHours() - p1.getHours())*60+p2.getMinutes() - p1.getMinutes());
                        Collections.sort(taskList, (p1, p2) -> (p2.getYear() - p1.getYear()) * 365 + (p2.getMonth() - p1.getMonth()) * 30 + p2.getDay() - p1.getDay());
                        startListView();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}