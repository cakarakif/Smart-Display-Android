package com.mabse.smartdisplay.LoginPage.ResetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mabse.smartdisplay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mabse.smartdisplay.R;

public class resetpass extends AppCompatActivity {

    private TextView signin;
    private Button reset;
    private EditText email;

    private FirebaseAuth auth;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        //ekranın yan olarak kullanılmasını engeller
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        define();
    }

    private void define() {
        //database başlatıldı.
        auth = FirebaseAuth.getInstance();

        signin = findViewById(R.id.signin);
        email=findViewById(R.id.email);
        reset=findViewById(R.id.button);

        rotate();
    }

    private void rotate() {
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(getApplicationContext(), com.mabse.smartdisplay.LoginPage.SignIn.signin.class);
                startActivity(intnt);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().equals("")){
                    resetPassword(email.getText().toString());
                }else
                    Toast.makeText(getApplicationContext(), R.string.fillArea, Toast.LENGTH_LONG).show();
            }
        });
    }

    //şifre sıfırlama işlemi
    private void resetPassword(String emailInfo) {
        loading = ProgressDialog.show(this,"Please wait...", "Retrieving data ...", true);
        auth.sendPasswordResetEmail(emailInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.dismiss();
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "Mail Gönderildi");
                    Toast.makeText(getApplicationContext(), R.string.resetSend, Toast.LENGTH_LONG).show();
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    Toast.makeText(getApplicationContext(), R.string.validMail, Toast.LENGTH_LONG).show();
                    email.setText("");
                }
            }
        });
    }
}
