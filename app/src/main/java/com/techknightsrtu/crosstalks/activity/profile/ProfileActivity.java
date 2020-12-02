package com.techknightsrtu.crosstalks.activity.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.techknightsrtu.crosstalks.BuildConfig;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.SplashActivity;
import com.techknightsrtu.crosstalks.activity.chat.HomeActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Widgets
    private ImageView ivBack, ivUserAvatar;
    private TextView tvLogOut, tvUserName,tvUserOriginalName, tvUserCollegeName, tvShareApp, tvRateUs;

    // Google banner ad
    private FrameLayout ad_view_container;

    // User Data Pref
    private UserProfileDataPref profileDataPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        // For Loading Ads
        GoogleAdMob googleAdMob = new GoogleAdMob(ProfileActivity.this, ad_view_container);
        googleAdMob.loadAd();

        setupUserProfile();
    }

    private void setupUserProfile() {
        ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(profileDataPref.getAvatarId())));
        tvUserName.setText(Avatar.nameList.get(Integer.parseInt(profileDataPref.getAvatarId())));
        String name = "(" + profileDataPref.getOriginalName() + ")";
        tvUserOriginalName.setText(name);
        tvUserCollegeName.setText(profileDataPref.getCollegeName());
    }

    private void init() {
        ivUserAvatar = findViewById(R.id.ivUserAvatar);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserOriginalName = findViewById(R.id.tvUserOriginalName);
        tvUserCollegeName = findViewById(R.id.tvUserCollegeName);

        tvShareApp = findViewById(R.id.tvShareApp);
        tvShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossTalks");
                    String shareMessage= "\nBored in this Quarantine ??? Let's have some fun, Download CrossTalks and chat with your mates anonymously.   \n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Invite your friends"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        tvRateUs = findViewById(R.id.tvRateUs);
        tvRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        ad_view_container = findViewById(R.id.ad_view_container);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvLogOut = findViewById(R.id.tvLogOut);
        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMethods.signOut();
                Intent i = new Intent(ProfileActivity.this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        profileDataPref = new UserProfileDataPref(ProfileActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}