package com.example.smartdisplay.Database;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdisplay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;

public class database {

    static FirebaseAuth auth;
    static FirebaseUser user;

    static FirebaseDatabase database;
    static DatabaseReference reference;

    static Boolean returnValue;

    public database() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Log.i("kontrol", "database başlatıldı");
        returnValue = false;
    }

    //kullanıcı giriş yapıp yapmadığının kontrolü
    static public boolean checkAuth() {
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

    //*****kullanıcı id'si ile database'e ona özel bilgiler ekleme
    static public void addInfo2Database() {
        //kullancıya özel database bilgi ekleme için eklendi
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("bilgi/" + user.getUid());

        Map map = new HashMap();
        map.put("boy", "197");
        map.put("yas", 26);
        reference.setValue(map);
    }

    //Kayıt için yönlendirildi.
    static public boolean register(String usrname, String pass) {
        returnValue=false;
        auth.createUserWithEmailAndPassword(usrname, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "başarılı");
                    returnValue = true;
                } else {
                    Log.i("hata", task.toString());
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    returnValue = false;
                }
            }
        });

        return returnValue;
    }

    //Giriş için yönlendirildi.
    static public boolean signInUser(String usrname, String pass) {
        returnValue=false;
        auth.signInWithEmailAndPassword(usrname, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol1", "başarılıLogin");
                    returnValue=true;
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol1", "başarısızLogin");
                    returnValue=false;
                }

            }
        });
        return returnValue;
    }

    //kullanıcı çıkışı
    static public void signOutUser() {
        auth.signOut();
    }

    //kullanıcı şifre değiştirmek isterse
    static public boolean changePassword(String newPass) {
        returnValue=false;
        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "başarılı");
                    returnValue=true;
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    returnValue=false;
                }

            }
        });

        return returnValue;
    }

    //mail doğrulandımı kontrolü
    static public boolean isEmailVerified() {
        return user.isEmailVerified();
    }

    //mail doğrulaması için gönderim
    static public boolean emailVerify() {
        returnValue=false;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "başarılı");
                    returnValue=true;
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    returnValue=false;
                }
            }
        });

        return returnValue;
    }

    //şifre sıfırlama işlemi
    static public boolean resetPassword(String email) {
        returnValue=false;
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //başarılı ise buraya düşer
                if (task.isSuccessful()) {
                    //sayfaya yönlendir
                    Log.i("kontrol", "Mail Gönderildi");
                    returnValue=true;
                } else {
                    //başarısız tepkisi ver
                    Log.i("kontrol", "başarısız");
                    returnValue=false;
                }
            }
        });

        return returnValue;
    }
}
