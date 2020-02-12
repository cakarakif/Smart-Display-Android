package com.example.smartdisplay.ui.settings;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdisplay.MainActivity;
import com.example.smartdisplay.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    View root;

    ImageView accountLogo,passwordLogo,helpLogo,feedbackLogo,aboutLogo,logoutLogo;
    TextView accountText,passwordText,helpText,feedbackText,aboutText,LogoutText;

    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        define();
        rotate();

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

    }

    private void rotate(){
        accountLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        accountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //////

        passwordLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        passwordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //////

        helpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        helpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //////

        feedbackLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        feedbackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //////

        aboutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        aboutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    }

    private void logout(){
        auth = FirebaseAuth.getInstance();
        auth.signOut();

        Toast.makeText(root.getContext(), R.string.LogoutSucces, Toast.LENGTH_LONG).show();

        Intent intnt=new Intent(root.getContext(), com.example.smartdisplay.LoginPage.SignIn.signin.class);
        startActivity(intnt);

    }

}
