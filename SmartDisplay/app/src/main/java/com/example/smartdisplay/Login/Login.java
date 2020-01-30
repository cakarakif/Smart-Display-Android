package com.example.smartdisplay.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;
import com.example.smartdisplay.ui.add.AddFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button login, register;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        defineVariables();
    }

    private void defineVariables() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();

        //Kayıt için yönlendirildi.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(username.getText().toString(), password.getText().toString());
            }
        });

        //Giriş için yönlendirildi.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(username.getText().toString(), password.getText().toString());
            }
        });
    }

    private void checkAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            //hata veya tepki verdir
        }else{

        }
    }

    private void register(String usrname, String pass) {
        auth.createUserWithEmailAndPassword(usrname, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Toast.makeText(getApplicationContext(), "başarılı", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("hata",task.toString());
                    //başarısız tepkisi ver
                    Toast.makeText(getApplicationContext(), "başarısız", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginUser(String usrname, String pass){
        auth.signInWithEmailAndPassword(usrname, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //sayfaya yönlendir
                    Toast.makeText(getApplicationContext(), "başarılıLogin", Toast.LENGTH_LONG).show();
                }else{
                    //başarısız tepkisi ver
                    Toast.makeText(getApplicationContext(), "başarısızLogin", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
