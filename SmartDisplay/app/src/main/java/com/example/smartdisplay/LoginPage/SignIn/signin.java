package com.example.smartdisplay.LoginPage.SignIn;

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

import com.example.smartdisplay.LoginPage.ResetPassword.resetpass;
import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin extends AppCompatActivity {

    private TextView forgotPass, signup;
    private TextView email, password;
    private Button button;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        define();
    }

    private void define() {
        //database başlatıldı.
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        forgotPass = findViewById(R.id.forgotPass);
        signup = findViewById(R.id.signup);
        button = findViewById(R.id.button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        rotate();
    }

    private void rotate() {

        //kullanıcı daha önceden giriş yapmış ise yönlendirme.
        //**Şuanlık fragmenlere geçerken hata veriyor normal activity çalışıyor.
        if(checkAuth()){
            Intent intnt=new Intent(this,MainActivity.class);
            startActivity(intnt);
        }

        //login kontrolü
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    signInUser(email.getText().toString(),password.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.fillArea, Toast.LENGTH_LONG).show();
                }

            }
        });

        //şifre yenilemeye yönlendirildi
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(getApplicationContext(), resetpass.class);
                startActivity(intnt);
            }
        });

        //kayıta yönlendirildi.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(getApplicationContext(), com.example.smartdisplay.LoginPage.SignUp.signup.class);
                startActivity(intnt);
            }
        });

    }

    //kullanıcı giriş yapıp yapmadığının kontrolü
    private boolean checkAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            //sayfaya yönlendir
            Log.i("kontrol", "kullanıcı sistemde");
            return true;
        } else {
            //sayfaya yönlendir
            Log.i("kontrol", "kullanıcı sistemde değil");
            return false;
        }
    }

    private void signInUser(String usrname, String pass) {
        loading = ProgressDialog.show(this,"Please wait...", "Retrieving data ...", true);
        auth.signInWithEmailAndPassword(usrname, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol1", "başarılıLogin");

                    Intent intnt=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intnt);
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol1", "başarısızLogin");

                    Toast.makeText(getApplicationContext(), R.string.incorrectInfo, Toast.LENGTH_LONG).show();
                    email.setText("");
                    password.setText("");
                }
                loading.dismiss();
            }
        });
    }
}
