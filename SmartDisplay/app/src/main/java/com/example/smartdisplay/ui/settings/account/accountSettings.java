package com.example.smartdisplay.ui.settings.account;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserInformation;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ui.settings.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class accountSettings extends Fragment {
    View root;

    private EditText nameEdit, surnameEdit, birthEdit, emailEdit;
    private Button cancel, save;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private ProgressDialog loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.account_settings_fragment, container, false);

        define();
        routing();


        return root;
    }

    private void define(){
        nameEdit=root.findViewById(R.id.nameEdit);
        surnameEdit=root.findViewById(R.id.surnameEdit);
        birthEdit=root.findViewById(R.id.birthEdit);
        emailEdit=root.findViewById(R.id.emailEdit);

        cancel=root.findViewById(R.id.cancel);
        save=root.findViewById(R.id.save);


        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference( user.getUid()+"/UserInfo/" );
    }

    private void routing(){
        //kayıtlı veri varsa ilk olarak çekilsin
        readUserInfo();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back2Setting();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameEdit.getText().toString().equals("") && !surnameEdit.getText().toString().equals("")
                        && !birthEdit.getText().toString().equals("")) {
                    saveUserInfo();
                }
                else{
                    Toast.makeText(root.getContext(), R.string.fillArea, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void back2Setting(){
        //fragmentler arası geçiş
        SettingsFragment frgmnt = new SettingsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.accountSettngs, frgmnt);
        fragmentTransaction.commit();
    }

    private void saveUserInfo(){
        UserInformation usrinfo=new UserInformation(birthEdit.getText().toString(),surnameEdit.getText().toString(),nameEdit.getText().toString());
        reference.setValue(usrinfo);
        Toast.makeText(root.getContext(), R.string.saveSuccess, Toast.LENGTH_LONG).show();
        back2Setting();
    }

    private void readUserInfo(){
        loading = ProgressDialog.show(getContext(),"Please wait...", "Retrieving data ...", true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    UserInformation usrinfo = dataSnapshot.getValue(UserInformation.class);

                    //alınan bilgileri textlere atama
                    nameEdit.setText(usrinfo.getName());
                    surnameEdit.setText(usrinfo.getSurname());
                    birthEdit.setText(usrinfo.getBirthday());
                    emailEdit.setText(user.getEmail().toString());

                }else{
                    UserInformation usrinfo = new UserInformation();
                    reference.setValue(usrinfo);
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



}
