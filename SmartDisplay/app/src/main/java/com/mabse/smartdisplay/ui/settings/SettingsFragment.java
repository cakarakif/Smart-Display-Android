package com.mabse.smartdisplay.ui.settings;

import androidx.appcompat.app.AlertDialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mabse.smartdisplay.R;
import com.mabse.smartdisplay.SmartScreen.SmartScreen;
import com.mabse.smartdisplay.SyncApps.Sync_Calendar;
import com.mabse.smartdisplay.ui.settings.welcome.WelcomeActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    View root;

    private ImageView accountLogo,passwordLogo,helpLogo,feedbackLogo,aboutLogo,logoutLogo;
    private TextView accountText,passwordText,helpText,feedbackText,aboutText,LogoutText;
    private Button ok;
    private ToggleButton syncCalendar;

    private ProgressDialog loading;

    private FirebaseAuth auth;

    private ToggleButton switchScreen;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        define();
        routing();

        return root;
    }

    private void define(){

        accountLogo=root.findViewById(R.id.accountLogo);
        passwordLogo=root.findViewById(R.id.passwordLogo);
        helpLogo=root.findViewById(R.id.helpLogo);
        feedbackLogo=root.findViewById(R.id.feedbackLogo);
        aboutLogo=root.findViewById(R.id.aboutLogo);
        logoutLogo=root.findViewById(R.id.logoutLogo);

        accountText=root.findViewById(R.id.accountText);
        passwordText=root.findViewById(R.id.passwordText);
        helpText=root.findViewById(R.id.helpText);
        feedbackText=root.findViewById(R.id.feedbackText);
        aboutText=root.findViewById(R.id.aboutText);
        LogoutText=root.findViewById(R.id.LogoutText);

        syncCalendar=root.findViewById(R.id.syncCalendar);
        switchScreen=root.findViewById(R.id.switchScreen);
    }

    private void routing(){
        accountLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSettings();
            }
        });
        accountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSettings();
            }
        });
        //////

        passwordLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });
        passwordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });
        //////

        helpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help();
            }
        });
        helpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help();
            }
        });
        //////

        feedbackLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
        feedbackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
        //////

        aboutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about();
            }
        });
        aboutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about();
            }
        });
        //////

        logoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        LogoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        ////Sync Calendar durumları oluşturuldu ve bağlandı
        Sync_Calendar syncclndr=new Sync_Calendar(root,getActivity());
        //syncclndr.listenSyncCalendarInfo();

        syncCalendar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
                    // Takvimi okuma izni verilmiş

                    if (!syncclndr.isFirstRead) {
                        try {
                            syncclndr.deleteCalendarTasks();//önce eskiler silinip
                            syncclndr.syncCalendar();//yeniler eklendi


                        } catch (Exception e) {
                        }
                    }
                    /*else if (!syncclndr.isFirstRead) {try { syncclndr.deleteCalendarTasks(); } catch (Exception e) {}}*/

                    if(!syncclndr.isFirstRead) {
                        //fragmentler arası geçiş
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.navigation_alltasks);
                    }
                }else {
                    // Takvimi okumaya izni verilmemiş
                    Toast.makeText(getContext(),"Allow the app to read the calendar!",Toast.LENGTH_LONG).show();
                    requestStoragePermission();
                }

            }
        });

        //switch screen
        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getContext(), SmartScreen.class);
                startActivity(intent);
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_CALENDAR)) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed because of reading calendar tasks.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[] {Manifest.permission.READ_CALENDAR}, 1);
                            Toast.makeText(getContext(),"Successfully saved!",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }
    }

    private void logout(){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(root.getContext(), R.string.LogoutSucces, Toast.LENGTH_LONG).show();

        Intent intnt=new Intent(root.getContext(), com.mabse.smartdisplay.LoginPage.SignIn.signin.class);
        startActivity(intnt);

    }

    private void changePass(){//şifre sıfırlama açılır ekran olarak ayarlandı.Tüm işlemler tek fonksiyonda yapıldı.

        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_changepass,null);
        //

        EditText email=view.findViewById(R.id.email);
        Button reset,cancel;
        reset=view.findViewById(R.id.reset);
        cancel=view.findViewById(R.id.cancel);

        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();
        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.90), (int)(getResources().getDisplayMetrics().heightPixels*0.50));
        //
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().equals("")){
                    resetPassword(email.getText().toString());
                }else
                    Toast.makeText(root.getContext(), R.string.fillArea, Toast.LENGTH_LONG).show();

                email.setText("");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogueShow.dismiss();
            }
        });
    }

    //şifre sıfırlama işlemi
    private void resetPassword(String emailInfo) {
        auth = FirebaseAuth.getInstance();
        loading = ProgressDialog.show(root.getContext(),"Please wait...", "Retrieving data ...", true);
        auth.sendPasswordResetEmail(emailInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.dismiss();
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "Mail Gönderildi");
                    Toast.makeText(root.getContext(), R.string.resetSend, Toast.LENGTH_LONG).show();
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    Toast.makeText(root.getContext(), R.string.validMail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void about(){
        //AlertDialogP1
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_about,null);
        //
        // ;
        ok=view.findViewById(R.id.ok);

        //AlertDialogP2
        AlertDialog.Builder alert=new AlertDialog.Builder(root.getContext());
        alert.setView(view);
        alert.setCancelable(true);
        AlertDialog dialogueShow=alert.create();
        dialogueShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogueShow.show();
        //ekran boyutlandırması
        dialogueShow.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.90), (int)(getResources().getDisplayMetrics().heightPixels*0.50));
        //

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogueShow.dismiss();
            }
        });
    }

    private void sendFeedback(){
        String deviceInfo =
                "Hello,"+"\nYOU CAN WRITE YOUR MESSAGE HERE" +
                "\n------------"+
                "\nDevice Info:" +
                "\n(for a better understanding of the feedback)" +
                "\nOS API Level: " + android.os.Build.VERSION.SDK_INT+
                "\nDevice: " + android.os.Build.DEVICE+
                "\nModel: " + android.os.Build.MODEL;//<br/><br/>

        Intent intnt=new Intent(Intent.ACTION_SENDTO);
        intnt.setData(Uri.parse("mailto:?subject=" + "Smart Display Feedback"+ "&body=" + ""+deviceInfo + "&to=" + "akifckr5@gmail.com"));

        try {
            startActivity(Intent.createChooser(intnt, "Send Email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_LONG).show();
        }

    }

    private void help(){
        Intent myIntent = new Intent(getContext(), WelcomeActivity.class);
        myIntent.putExtra("isClickedHelp",true);
        startActivity(myIntent);
    }

    private void accountSettings(){
        //Eski Yöntem(Bir önceki fragment tıklanabiliyor oluyor bunda.
        /*accountSettings fragment = new accountSettings();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.profile, fragment)
                .addToBackStack(null)
                .commit();
        */

        //fragmentler arası geçiş
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_accountSettings);
        //mobile_navigation.xml'ine id'yi ekledikten sonra çağırılabiliyor.
    }
}
