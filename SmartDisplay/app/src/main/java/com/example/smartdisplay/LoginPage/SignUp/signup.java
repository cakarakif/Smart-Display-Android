package com.example.smartdisplay.LoginPage.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.LoginPage.SignIn.signin;
import com.example.smartdisplay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    private TextView signin;
    private TextView email, password;
    private Button button;

    private FirebaseAuth auth;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        define();
    }

    private void define() {
        //database başlatıldı.
        auth = FirebaseAuth.getInstance();

        signin= findViewById(R.id.signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);

        rotate();
    }

    private void rotate() {
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(getApplicationContext(), com.example.smartdisplay.LoginPage.SignIn.signin.class);
                startActivity(intnt);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    register(email.getText().toString(),password.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter all necessary information!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //Kayıt için yönlendirildi.
    private void register(String usrname, String pass) {
        loading = ProgressDialog.show(this,"Please wait...", "Retrieving data ...", true);
        auth.createUserWithEmailAndPassword(usrname, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.dismiss();
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "başarılı");
                    Toast.makeText(getApplicationContext(), "Register Successful!", Toast.LENGTH_LONG).show();
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    Toast.makeText(getApplicationContext(), "Enter a valid mail!", Toast.LENGTH_LONG).show();
                    email.setText("");
                    password.setText("");
                }
            }
        });
    }
}
