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
import com.example.smartdisplay.SyncApps.Sync_Facebook;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.Arrays;

public class signin extends AppCompatActivity {

    private TextView forgotPass, signup;
    private TextView email, password;
    private Button button;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ProgressDialog loading;


    LoginButton facebook;
    CallbackManager callbackManager;

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


        facebook=findViewById(R.id.facebook);

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

        ///////Facebook
        routingFacebook();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//facebook sekmesinden sonra düşen kısım-zorunlu
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void routingFacebook(){

        // Facebook SDK init
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        facebook.setReadPermissions(Arrays.asList("email", "public_profile"));

        facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("akifControl", loginResult.getRecentlyGrantedPermissions()+"");
                        handleFacebookAccessToken(loginResult.getAccessToken());

                        //izin aldıktan sonra isteğe göre cevap gelir
                        //fakat facebook sınırlamaları yüzünden istenilen herşey gelmez
                        GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Log.i("asdasd",response.getError()+"");
                                        } else {
                                            Log.i("asdasd",me+"");
                                            Log.i("asdasd",response+"");
                                            String email = me.optString("email");
                                            String name = me.optString("name");
                                            String birthday = me.optString("birthday");
                                            String gender = me.optString("gender")+"-";
                                            String events = me.optString("events")+"-";

                                            Log.i("asdasd",email);
                                            Log.i("asdasd",name);
                                            Log.i("asdasd",birthday);
                                            Log.i("asdasd",gender+"123");
                                            Log.i("asdasd",events+"123");
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender, birthday");//neleri döndürsün
                        mGraphRequest.setParameters(parameters);
                        mGraphRequest.executeAsync();
                    }


                    @Override
                    public void onCancel () {
                        Log.i("akifControl", "Login attempt cancelled.");
                    }

                    @Override
                    public void onError (FacebookException e){
                        e.printStackTrace();
                        Log.i("akifControl", "Login attempt failed.");
                    }
                }
        );
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("kntrl", "signInWithCredential:success");
                            user = auth.getCurrentUser();

                            Intent intnt=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intnt);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("kntrl", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
