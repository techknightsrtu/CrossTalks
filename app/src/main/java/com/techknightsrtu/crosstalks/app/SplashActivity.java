package com.techknightsrtu.crosstalks.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.auth.LoginActivity;
import com.techknightsrtu.crosstalks.app.feature.auth.RegistrationActivity;
import com.techknightsrtu.crosstalks.app.feature.home.HomeActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ImageView ivlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivlogo = findViewById(R.id.ivlogo);

        ivlogo.animate().scaleX(4).scaleY(4).setDuration(500).start();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            // ad pref
            SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
            SharedPreferences.Editor adeditor = pref.edit();
            adeditor.putBoolean("showAd",true);
            adeditor.commit();

            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

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
        }, 1000);

    }


}