package com.example.smartdisplay.LoginPage.SignIn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.LoginPage.ResetPassword.resetpass;
import com.example.smartdisplay.LoginPage.SignUp.signup;
import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;

public class signin extends AppCompatActivity {

    private TextView forgotPass, signup;
    private TextView email, password;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        define();
    }

    private void define() {
        forgotPass = findViewById(R.id.forgotPass);
        signup = findViewById(R.id.signup);
        button = findViewById(R.id.button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        rotate();
    }

    private void rotate() {
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

        //login kontrolü
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {

                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter all necessary information!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
