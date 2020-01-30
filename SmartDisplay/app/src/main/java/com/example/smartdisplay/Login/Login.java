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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button login, register, addInfo;

    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference reference;

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
        addInfo = findViewById(R.id.addInfo);

        auth = FirebaseAuth.getInstance();

        //kullanıcı giriş yapıp yapmadığının kontrolü
        checkAuth();

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

        addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInfo2Database();
            }
        });
    }

    private void checkAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            //sayfaya yönlendir
            Toast.makeText(getApplicationContext(), "kullanıcı sistemde", Toast.LENGTH_LONG).show();
        }else{
            //sayfaya yönlendir
            Toast.makeText(getApplicationContext(), "kullanıcı sistemde değil", Toast.LENGTH_LONG).show();
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

    //kullanıcı id'si ile database'e ona özel bilgiler ekleme
    private void addInfo2Database(){
        //kullancıya özel database bilgi ekleme için eklendi
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("bilgi/"+user.getUid());

        Map map=new HashMap();
        map.put("boy","180");
        map.put("yas",26);
        reference.setValue(map);
    }
}
