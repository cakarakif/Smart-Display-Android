package com.mabse.smartdisplay.SyncApps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

public class Sync_Facebook_kullanilmiyor {/////ŞUANLIK BİRYERDE KULLANILMIYOR
    private View root;

    // Constructor
    public Sync_Facebook_kullanilmiyor(View root) {
        this.root = root;
    }

    public void saveAccessToken(String token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }


    public String getToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        Log.i("AkifCntrl",prefs.getString("fb_access_token", null));
        return prefs.getString("fb_access_token", null);
    }

    public void clearToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveFacebookUserInfo(String id,String first_name,String last_name, String email){//kayıt edilen bilgileri getFacebook'tanda güncelle
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_id", id);
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_last_name", last_name);
        editor.putString("fb_email", email);
        editor.apply(); // This line is IMPORTANT !!!
        Log.i("AkifCntrl", "Shared Name : "+first_name+"\nLast Name : "+last_name+"\nEmail : "+email+"\nid : "+id);
    }

    public void getFacebookUserInfo(){//Bundle olarakda geri gönderilebilir-ihtiyaca göre
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        Log.i("AkifCntrl", "Name : "+prefs.getString("fb_first_name",null)+" "+prefs.getString("fb_last_name",null)+"\nEmail : "+prefs.getString("fb_email",null)
                +"\nID : "+prefs.getString("fb_id",null));
    }

    public Bundle parseFacebookData(JSONObject object) {
        Log.i("AkfJsonControl",object+"");
        Bundle bundle = new Bundle();//bundlesız direkt aşağıda kaydedilebilir. Ek bir durum yani

        try {
            String id = object.getString("id");


            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));


            //kaydedildiği kısım
            saveFacebookUserInfo(id,object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"));

        } catch (Exception e) {
            Log.i("AkifCntrl", "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }
}
