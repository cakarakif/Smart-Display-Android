package com.mabse.smartdisplay.ui.add;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mabse.smartdisplay.DatabaseHelperClasses.UserInformation;
import com.mabse.smartdisplay.DatabaseHelperClasses.UserTask;
import com.mabse.smartdisplay.R;
import com.mabse.smartdisplay.ReminderAlarm.AddReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFragment extends Fragment {
    private View root;
    //private AdView mAdView;

    private CheckBox repeatLogo, onceLogo, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TextView selectTime, timeText, repeatText, onceText, selectDate,dateText,addTitle;
    private RadioGroup typeRadios;
    private RadioButton radioOne,radioTwo;
    private EditText typeEdit, nameEdit,descEdit;
    private ScrollView scroll;
    private LinearLayout days, dateArea;
    private ImageView dateLogo;
    private Button save;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private ProgressDialog loading;
    private int counter=1;
    private Boolean blockDouble;

    private UserTask editTask;//edit ile geldiyse dolar


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add, container, false);

        define();
        routing();

        return root;
    }

    private void define(){
        addTitle=root.findViewById(R.id.addTitle);

        repeatLogo=root.findViewById(R.id.repeatLogo);
        monday=root.findViewById(R.id.monday);
        tuesday=root.findViewById(R.id.tuesday);
        wednesday=root.findViewById(R.id.wednesday);
        thursday=root.findViewById(R.id.thursday);
        friday=root.findViewById(R.id.friday);
        saturday=root.findViewById(R.id.saturday);
        sunday=root.findViewById(R.id.sunday);

        selectTime=root.findViewById(R.id.selectTime);
        timeText=root.findViewById(R.id.timeText);

        typeRadios=root.findViewById(R.id.typeRadio);
        radioOne=root.findViewById(R.id.radioOne);
        radioTwo=root.findViewById(R.id.radioTwo);
        typeEdit=root.findViewById(R.id.typeEdit);

        scroll=root.findViewById(R.id.scroll);
        onceLogo=root.findViewById(R.id.onceLogo);

        days=root.findViewById(R.id.days);
        dateArea=root.findViewById(R.id.dateArea);
        repeatText=root.findViewById(R.id.repeatText);
        onceText=root.findViewById(R.id.onceText);

        dateLogo=root.findViewById(R.id.dateLogo);
        selectDate=root.findViewById(R.id.selectDate);
        dateText=root.findViewById(R.id.dateText);
        //ilk açılışta datetext'i için tarih çekildi.
        final Calendar cldr = Calendar.getInstance();
        selectDate.setText(convertDateString(cldr.get(Calendar.DAY_OF_MONTH),(cldr.get(Calendar.MONTH)+1),cldr.get(Calendar.YEAR)));

        save=root.findViewById(R.id.save);

        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        nameEdit=root.findViewById(R.id.nameEdit);
        descEdit=root.findViewById(R.id.descEdit);

        /*mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

    }

    private void routing(){

        repeatLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(repeatLogo.isChecked()){

                    days.setVisibility(View.VISIBLE);
                    dateArea.setVisibility(View.INVISIBLE);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    onceLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    onceLogo.setChecked(false);
                }else {

                    days.setVisibility(View.INVISIBLE);
                    dateArea.setVisibility(View.VISIBLE);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    onceLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    onceLogo.setChecked(true);
                }
            }
        });

        onceLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onceLogo.isChecked()){

                    days.setVisibility(View.INVISIBLE);
                    dateArea.setVisibility(View.VISIBLE);
                    onceLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    repeatLogo.setChecked(false);

                }else {

                    days.setVisibility(View.VISIBLE);
                    dateArea.setVisibility(View.INVISIBLE);
                    onceLogo.setBackgroundResource(R.drawable.ic_radioempty);
                    repeatLogo.setBackgroundResource(R.drawable.ic_radiofill);
                    repeatLogo.setChecked(true);
                }
            }
        });

        repeatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatLogo.performClick();
            }
        });

        onceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onceLogo.performClick();
            }
        });

        daysClicked();//hangi günlerin seçildiği dinlenildi.

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scroll.smoothScrollTo(0, scroll.getBottom());
                selectTime();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scroll.smoothScrollTo(0, scroll.getBottom());
                selectTime();
            }
        });

        typeRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                scroll.smoothScrollTo(0, scroll.getBottom());//save buttonu gözükmesi için scroll yapıldı

                int selectedRadio = typeRadios.getCheckedRadioButtonId();

                if(selectedRadio == R.id.radioOne){
                    typeEdit.setVisibility(View.GONE);
                }else if(selectedRadio == R.id.radioTwo){
                    typeEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        dateLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readUserCounterInfo();
            }
        });

        if(isEditableValue()){//Eğer edit ile geldiyse
            fillPageEdit();
        }
    }

    private void daysClicked(){
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monday.isChecked()){
                    setBackgroundCB(monday,R.drawable.daymm);
                }else {
                    setBackgroundCB(monday,R.drawable.daym);
                }
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tuesday.isChecked()){
                    setBackgroundCB(tuesday,R.drawable.daytt);
                }else {
                    setBackgroundCB(tuesday,R.drawable.dayt);
                }
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wednesday.isChecked()){
                    setBackgroundCB(wednesday,R.drawable.dayww);
                }else {
                    setBackgroundCB(wednesday,R.drawable.dayw);
                }
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thursday.isChecked()){
                    setBackgroundCB(thursday,R.drawable.daytt);
                }else {
                    setBackgroundCB(thursday,R.drawable.dayt);
                }
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friday.isChecked()){
                    setBackgroundCB(friday,R.drawable.dayff);
                }else {
                    setBackgroundCB(friday,R.drawable.dayf);
                }
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saturday.isChecked()){
                    setBackgroundCB(saturday,R.drawable.dayss);
                }else {
                    setBackgroundCB(saturday,R.drawable.days);
                }
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sunday.isChecked()){
                    setBackgroundCB(sunday,R.drawable.dayss);
                }else {
                    setBackgroundCB(sunday,R.drawable.days);
                }
            }
        });
    }

    private void setBackgroundCB(CheckBox whichone, int background){//genelleştirme için yardımcı fonksiyon
        whichone.setBackgroundResource(background);
    }

    private void readUserCounterInfo(){//counter yapısını datebasede oluşturarak taskların sıralamasını beliledik.
        //eğer cihazda notification kanalı kapalıysa kontrol yapıldı
        AddReminder ad = new AddReminder(getContext());

        if(isFormValid() && ad.isNotificationsActiveOnDevice()) {
            blockDouble=true;//reference'de olan değişkeni değiştirdiğimiziçin onDataChange iki kere düşmesini engelledik.
            loading = ProgressDialog.show(getContext(), "Please wait...", "Saving...", true);
            reference = database.getReference(user.getUid() + "/counter");

            if(!isEditableValue()) {//edit mi new mi durumu kontrolü
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null && blockDouble) {
                            blockDouble = false;
                            counter = Integer.parseInt(dataSnapshot.getValue().toString());
                            counter++;
                            reference.setValue(counter);
                            saveUserInfo();
                        } else if (blockDouble) {
                            blockDouble = false;
                            reference.setValue(counter);
                            saveUserInfo();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //showToast("" + R.string.controlInternet);
                        loading.dismiss();
                    }
                });
            }
            else{
                saveUserInfo();
            }


        }
    }

    private void saveUserInfo(){
        reference = database.getReference( user.getUid()+"/Tasks/"+counter);//nereye kaydedileceğinin bilgisi.



        UserTask usrtask= new UserTask(true,nameEdit.getText().toString(),descEdit.getText().toString(),"0",
                repeatInfoForDatebase(),repeatLogo.isChecked(),selectTime.getText().toString(),radioOne.isChecked(),typeEdit.getText().toString(),String.valueOf(counter));
        reference.setValue(usrtask);

        loading.dismiss();
        showToast(""+getString(R.string.saveSuccess));
        back2AllTasks();

    }

    private boolean isFormValid(){
        Log.i("kontrol",nameEdit.getText()+"");

        if(nameEdit.getText().toString().equals("")){
            showToast(""+getString(R.string.titleMissing));
            return false;
        }
        if (repeatLogo.isChecked() && !monday.isChecked() && !tuesday.isChecked()
                && !wednesday.isChecked() && !thursday.isChecked() && !friday.isChecked() && !saturday.isChecked() && !sunday.isChecked()){
            showToast(""+getString(R.string.dateMissing));
            return false;
        }

        int selectedRadio = typeRadios.getCheckedRadioButtonId();
        if((selectedRadio == R.id.radioTwo) && typeEdit.getText().toString().equals("")){
            showToast(""+getString(R.string.typeEmpty));
            return false;
        }
        return true;
    }

    private void back2AllTasks(){//Task eklenince taskların listelendiği ekrana yönlendirildi.
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_alltasks);
    }

    private boolean isEditableValue(){//Eğer edit ile geldiyse onun kontrolü
        try {
            editTask = (UserTask) getArguments().getSerializable("selectedTask");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void fillPageEdit(){//edit edilmek istenen bilgiler sayfaya dolduruldu.
        addTitle.setText(getString(R.string.updateTitle));
        save.setBackgroundResource(R.drawable.updatelong);

        counter=Integer.parseInt(editTask.getId());//update için reference counterı değiştirildi.

        nameEdit.setText(editTask.getTitle());
        descEdit.setText(editTask.getDescription());
        selectTime.setText(editTask.getTime());

        if(editTask.getAlertType()){radioOne.setChecked(true);}
        else {
            radioTwo.setChecked(true);
            typeEdit.setText(editTask.getVideoUrl());
            typeEdit.setVisibility(View.VISIBLE);
        }


        if(editTask.getRepeatType()){
            //repeatLogo.performClick();
            String[] tokens = editTask.getRepeatInfo().split("/");

            setAllDaysNotClicked();
            for (String t : tokens)
                if(t.equals("Mon"))
                    monday.performClick();
                else if(t.equals("Tue"))
                    tuesday.performClick();
                else if(t.equals("Wed"))
                    wednesday.performClick();
                else if(t.equals("Thu"))
                    thursday.performClick();
                else if(t.equals("Fri"))
                    friday.performClick();
                else if(t.equals("Sat"))
                    saturday.performClick();
                else if(t.equals("Sun"))
                    sunday.performClick();
        } else {
            onceLogo.performClick();
            selectDate.setText(editTask.getRepeatInfo());
        }
    }

    private void setAllDaysNotClicked(){//Repeat buttonu için işlem atandı.//Şuan Kullanılmıyor
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);
        sunday.setChecked(false);

        setBackgroundCB(monday,R.drawable.daym);
        setBackgroundCB(tuesday,R.drawable.dayt);
        setBackgroundCB(wednesday,R.drawable.dayw);
        setBackgroundCB(thursday,R.drawable.dayt);
        setBackgroundCB(friday,R.drawable.dayf);
        setBackgroundCB(saturday,R.drawable.days);
        setBackgroundCB(sunday,R.drawable.days);
    }

    private String repeatInfoForDatebase(){//database yazılmak üzere seçilen tarih  için kalıp oluşturuldu.
        String info="";

        if(repeatLogo.isChecked()){
            if(monday.isChecked())
                info +="Mon/";
            if(tuesday.isChecked())
                info +="Tue/";
            if(wednesday.isChecked())
                info +="Wed/";
            if(thursday.isChecked())
                info +="Thu/";
            if(friday.isChecked())
                info +="Fri/";
            if(saturday.isChecked())
                info +="Sat/";
            if(sunday.isChecked())
                info +="Sun/";

            info = info.substring(0, info.length() - 1);
        }else{
            info += selectDate.getText().toString();
        }

        return info;
    }

    private void showToast(String message){
        Toast.makeText(root.getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void selectTime(){
        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_timepicker,null);
        //

        TimePicker picker;
        Button btnGet;;

        picker=(TimePicker)view.findViewById(R.id.datePicker);
        picker.setIs24HourView(true);
        btnGet=(Button)view.findViewById(R.id.select);

        //seçileni time picker başlangı olarak atadık.
        String[] times = selectTime.getText().toString().split(":");
        picker.setHour(Integer.parseInt(times[0]));
        picker.setMinute(Integer.parseInt(times[1]));


        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        //dialog dissmiss edilirse burası çalışır.
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                selectTime.setText(((picker.getHour() < 10) ? "0"+picker.getHour(): picker.getHour()) +":"+
                        ((picker.getMinute() < 10 ) ? "0"+picker.getMinute(): picker.getMinute()));
            }
        });
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();
        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.60), (int)(getResources().getDisplayMetrics().heightPixels*0.30));

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogueShow.dismiss();//yukarıdaki dismiss yakalamaya düşüyor.
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
        picker.setMinDate(System.currentTimeMillis() - 1000);

        //seçileni date picker başlangı olarak atadık.
        String[] date = selectDate.getText().toString().split("/");
        picker.init(Integer.parseInt(date[2]), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]), null);




        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        //dialog dissmiss edilirse burası çalışır.
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                selectDate.setText(convertDateString(picker.getDayOfMonth(),picker.getMonth() + 1,picker.getYear()));
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
