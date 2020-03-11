package com.example.smartdisplay.ui.settings.account;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserInformation;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ui.settings.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class accountSettings extends Fragment {
    View root;

    private EditText nameEdit, surnameEdit, birthEdit, emailEdit;
    private Button cancel, save;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private Calendar cldr ;

    private ProgressDialog loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_account_settings, container, false);

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

        cldr = Calendar.getInstance();
    }

    private void routing(){
        //kayıtlı veri varsa ilk olarak çekilsin
        readUserInfo();

        birthEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

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
                //Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        });
    }

    private void selectDate(){
        ///*****///
        //uygulama dili ingilizce olarak ayarlandıki tarihler ona göre gelsin
        Locale.setDefault(Locale.ENGLISH);
        Configuration configuration = root.getResources().getConfiguration();
        configuration.setLocale(Locale.ENGLISH);
        configuration.setLayoutDirection(Locale.ENGLISH);
        root.getContext().createConfigurationContext(configuration);
        //////////////////

        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_datepicker,null);
        //

        DatePicker picker;
        Button btnGet;;

        picker=(DatePicker)view.findViewById(R.id.datePicker);
        btnGet=view.findViewById(R.id.select);

        //seçileni date picker başlangı olarak atadık.
        String[] date = birthEdit.getText().toString().split("/");
        if(date.length == 2)
            picker.init(Integer.parseInt(date[2]), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]), null);
        else//eğer girilmemişse bugünün tarihi olarak picker başlatıldı
            picker.init(Integer.parseInt(cldr.get(Calendar.YEAR)+"")-18,Integer.parseInt(cldr.get(Calendar.MONTH)+""),Integer.parseInt(cldr.get(Calendar.DAY_OF_MONTH)+""), null);



        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        //dialog dissmiss edilirse burası çalışır.
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                birthEdit.setText(convertDateString(picker.getDayOfMonth(),picker.getMonth() + 1,picker.getYear()));
            }
        });
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();

        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.80), (int)(getResources().getDisplayMetrics().heightPixels*0.55));


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogueShow.dismiss();
            }
        });

    }

    private String convertDateString(int day, int month, int year){//0-9 arası sayılar 00/01 tarzı gösterimi için
        return  ((day < 10 ) ? "0"+day: day )+"/"+((month < 10) ? "0"+month: month)+"/"+year;
    }

}
