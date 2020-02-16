package com.example.smartdisplay.ui.settings.account;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdisplay.R;
import com.example.smartdisplay.ui.settings.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    }

    private void routing(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fragmentler arası geçiş
                SettingsFragment frgmnt = new SettingsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.accountSettngs, frgmnt);
                fragmentTransaction.commit();
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

    private void saveUserInfo(){
        //kullancıya özel database bilgi ekleme için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference( user.getUid()+"/UserInfo/" );

        Map map = new HashMap();
        map.put("name", nameEdit.getText().toString());
        map.put("surname", surnameEdit.getText().toString());
        map.put("birthday", birthEdit.getText().toString());
        reference.setValue(map);


    }



}
