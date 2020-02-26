package com.example.smartdisplay.ui.alltasks;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import java.util.Collection;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private View root;

    private List<UserTask> taskList;
    private List<UserTask> tempSearchtaskList;
    private TaskListAdapter listAdapter;
    private ListView listView;

    private ProgressDialog loading;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private EditText search;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_alltasks, container, false);

        define();
        readUserTasks();

        return root;
    }

    private void define() {
        listView = root.findViewById(R.id.taskList);

        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user.getUid() + "/Tasks");

        search=root.findViewById(R.id.search);
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

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserTask usrtasks = postSnapshot.getValue(UserTask.class);
                        taskList.add(usrtasks);
                    }

                    tempSearchtaskList = new ArrayList<>(taskList);//search için yedeklendi
                    searchText();//search aktif edildi.

                    //list'in nasıl görüneceğinin adapterı
                    listAdapter = new TaskListAdapter(taskList, getContext());

                    //bağlama işlemi yaptık
                    listView.setAdapter(listAdapter);


                } else {
                    //UserInformation usrinfo = new UserInformation();
                    //reference.setValue(usrinfo);
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

    private void searchText(){
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
}