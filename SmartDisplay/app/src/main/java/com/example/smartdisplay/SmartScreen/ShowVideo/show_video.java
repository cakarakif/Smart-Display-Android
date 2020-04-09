package com.example.smartdisplay.SmartScreen.ShowVideo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.smartdisplay.DatabaseHelperClasses.DatabaseProcessing;
import com.example.smartdisplay.DatabaseHelperClasses.UserTask;
import com.example.smartdisplay.R;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class show_video extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private Button cancel;
    public static final String API_KEY = "AIzaSyC-gAnOcLsmDraMPkfnWVH9eTFsEDQ3VyQ";
    public static String VIDEO_ID = "2MpUj-Aua48";
    YouTubePlayer player;
    YouTubePlayerView youTubePlayerView;
    UserTask usrTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        Intent intent = getIntent();
        Gson gson = new Gson();
        usrTask = gson.fromJson(intent.getStringExtra("usrTask"), UserTask.class);

        define();
        setContentView();

        //video url aktif edildiyse göster ve bildirim ver yoksa direkt bildirim ver.
        if (!usrTask.getAlertType()) {
            VIDEO_ID=parseUrl();
            showVideo();
        }else{
            cancel.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.GONE);
            areUSureTickCompleted();
        }
    }

    private void define(){
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        cancel=findViewById(R.id.cancel);
    }

    private String parseUrl(){
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(usrTask.getVideoUrl()); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return ""+matcher.group();
        }
        return "EpEIieIdToU";//eger bulamazsa
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setContentView() {

        //Tam ekran kullanımı için
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ekranın yan olarak kullanılmasını sağlar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //ekranın yanlarına basınca activity kapanmasın
        setFinishOnTouchOutside(false);


    }

    private void showVideo(){
        /** Initializing YouTube Player View **/
        youTubePlayerView.initialize(API_KEY, this);

        //cancele basınca dialog gelsin
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areUSureTickCompleted();
            }
        });
    }

    private void areUSureTickCompleted() {
        DatabaseProcessing dtbs=new DatabaseProcessing();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dtbs.increaseGoalNumber(usrTask.getId(),usrTask.getGoal());
                        Toast.makeText(getApplicationContext(), "Completed!", Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), "Not Completed!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        String message="Title: "+usrTask.getTitle() + "\n"
                +"Description: "+ usrTask.getDescription()  + "\n"
                + "\n"+"Did you finish the task?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.colorAccent));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.yellow));
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player=player;
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        player.cueVideo(VIDEO_ID);
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
        }
        @Override
        public void onPlaying() {
        }
        @Override
        public void onSeekTo(int arg0) {
        }
        @Override
        public void onStopped() {
        }
    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
            if(player != null)
                player.play(); //auto play
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
        }
        @Override
        public void onVideoStarted() {
        }
    };
}
