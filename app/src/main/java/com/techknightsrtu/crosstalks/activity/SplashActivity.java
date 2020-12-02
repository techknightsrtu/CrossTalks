package com.techknightsrtu.crosstalks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.auth.LoginActivity;
import com.techknightsrtu.crosstalks.activity.auth.RegistrationActivity;
import com.techknightsrtu.crosstalks.activity.chat.HomeActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;

import java.util.Timer;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ImageView ivlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivlogo = findViewById(R.id.ivlogo);

        ivlogo.animate().scaleX(4).scaleY(4).setDuration(500).start();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                MobileAds.initialize(SplashActivity.this);

                if(FirebaseMethods.isUserSignedIn()){

                    FirebaseMethods.checkIfUserExist(FirebaseMethods.getUserId(), exist -> {

                        if(exist){

                            Log.d(TAG, "onCallback: LOGIN SUCCESS");
                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }else{
                            //send user to gender activity
                            startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
                        }

                    });


                }else{

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();

                }
            }
        }, 2000);

    }


}