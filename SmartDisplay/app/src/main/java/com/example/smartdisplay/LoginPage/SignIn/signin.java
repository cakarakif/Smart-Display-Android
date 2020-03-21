package com.example.smartdisplay.LoginPage.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.DatabaseHelperClasses.UserInformation;
import com.example.smartdisplay.LoginPage.ResetPassword.resetpass;
import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Arrays;

public class signin extends AppCompatActivity {

    private TextView forgotPass, signup;
    private TextView email, password;
    private Button button;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    DatabaseReference reference;

    private ProgressDialog loading;


    LoginButton facebook;
    CallbackManager callbackManager;

    GoogleSignInClient mGoogleSignInClient;
    SignInButton google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ekranın yan olarak kullanılmasını engeller
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        define();
    }

    private void define() {
        controlNetConnection();
        //database başlatıldı.
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database= FirebaseDatabase.getInstance();

        forgotPass = findViewById(R.id.forgotPass);
        signup = findViewById(R.id.signup);
        button = findViewById(R.id.button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        facebook=findViewById(R.id.facebook);
        google=findViewById(R.id.google);

        rotate();


    }

    private boolean controlNetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
            return false;
        }
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


                if (!email.getText().toString().equals("") && !password.getText().toString().equals("") && controlNetConnection()) {
                    signInUser(email.getText().toString(),password.getText().toString());
                }
                else if(controlNetConnection()){
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

        ///////Google
        routingGoogle();
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
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

                        //izin aldıktan sonra isteğe göre cevap gelir
                        //fakat facebook sınırlamaları yüzünden istenilen herşey gelmez
                        GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Log.i("asdasd",response.getError()+"");
                                        } else {
                                            String birthday="";
                                            if(me.optString("birthday").equals("")) {
                                                String[] birthParts = (me.optString("birthday")).split("/");
                                                birthday = birthParts[1] + "/" + birthParts[0] + "/" + birthParts[2];
                                            }


                                            UserInformation usrinfo=new UserInformation(birthday,me.optString("last_name"),me.optString("first_name"));
                                            handleFacebookAccessToken(loginResult.getAccessToken(),usrinfo);
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

    private void handleFacebookAccessToken(AccessToken token,UserInformation usrinfo) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("kntrl", "signInWithCredential:success");
                            user = auth.getCurrentUser();

                            reference = database.getReference( user.getUid()+"/UserInfo/" );
                            reference.setValue(usrinfo);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {//google yakalandı
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i("kntrl", "Google sign in failed", e);
            }
        }else{//facebook yakalandı
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void routingGoogle(){
        //Başlık stringi atandı
        TextView textView = (TextView) google.getChildAt(0);
        textView.setText("Continue with Google");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.i("kntrl", "firebaseAuthWithGoogle:"+acct.getGivenName());//sınırlı kullanıcı bilgisi geliyor(isim-email falan)

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
