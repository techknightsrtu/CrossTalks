package com.techknightsrtu.crosstalks.app.feature.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.DialogCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.techknightsrtu.crosstalks.BuildConfig;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.home.adapter.MyFragmentPagerAdapter;
import com.techknightsrtu.crosstalks.app.feature.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetVersionDetails;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;

import java.util.Calendar;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    //LocalData
    UserProfileDataPref prefs;

    //Widgets
    ViewPager viewPager;

    // Google banner ad
    private FrameLayout ad_view_container;

    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        init();
        setupBottomNavigationBar();

        aDayCompleted();

        // For Loading Ads
        SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
        if (pref.getBoolean("showAd", false)) {
            loadRewardAd();
            GoogleAdMob googleAdMob = new GoogleAdMob(HomeActivity.this, ad_view_container);
            googleAdMob.loadAd();
        }

    }

    private void init(){

        ad_view_container = findViewById(R.id.ad_view_container);

        prefs = new UserProfileDataPref(HomeActivity.this);

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);

        tvCollegeName.setText(prefs.getCollegeName());

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), getChangingConfigurations()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                    setCurrentSelectedItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }


    private void setupBottomNavigationBar(){

        ImageView chatLabel = findViewById(R.id.ivChatIcon);
        ImageView onlineLabel = findViewById(R.id.ivOnlineIcon);
        ImageView profileLabel = findViewById(R.id.ivProfileIcon);

        profileLabel.setImageResource(Avatar.avatarList.get(Integer.parseInt(prefs.getAvatarId())));
        profileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


        chatLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
                setCurrentSelectedItem(0);
            }
        });

        onlineLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               viewPager.setCurrentItem(1);
               setCurrentSelectedItem(1);

            }
        });

    }

    private void setCurrentSelectedItem(int currPos){

        ImageView chatLabel = findViewById(R.id.ivChatIcon);
        ImageView onlineLabel = findViewById(R.id.ivOnlineIcon);

        if(currPos == 0){
            chatLabel.setImageResource(R.drawable.ic_chats_selected);
            onlineLabel.setImageResource(R.drawable.ic_online_not_selected);

        }else if(currPos == 1){
            chatLabel.setImageResource(R.drawable.ic_chats_not_selected);
            onlineLabel.setImageResource(R.drawable.ic_online_selected);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus("Online");
    }

    @Override
    protected void onPause() {
        FirebaseMethods.setUserOnlineStatus("Offline");
        super.onPause();
    }

    // for remove ad onclick listener
    public void removeAd(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View removeAdDialogView = factory.inflate(R.layout.remove_ad_dialog, null);
        final AlertDialog removeAdDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.CustomDialogTheme)
        ).create();
        removeAdDialog.setView(removeAdDialogView);
        Window window = removeAdDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvRemoveAd = removeAdDialogView.findViewById(R.id.tvRemoveAd);
        TextView tvAdRemoved = removeAdDialogView.findViewById(R.id.tvAdRemoved);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
        if (pref.getBoolean("showAd", false)) {
            tvAdRemoved.setVisibility(View.GONE);
            tvRemoveAd.setVisibility(View.VISIBLE);
        }else{
            tvAdRemoved.setVisibility(View.VISIBLE);
            tvRemoveAd.setVisibility(View.GONE);
        }

        tvRemoveAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic

                if(rewardedAd.isLoaded()){
                    Activity activityContext = HomeActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // User earned reward.
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
                            SharedPreferences.Editor adeditor = pref.edit();
                            adeditor.putBoolean("showAd",false);
                            adeditor.commit();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                }else{
                    Toast.makeText(HomeActivity.this, "Try again after some time.", Toast.LENGTH_SHORT).show();
                }
                removeAdDialog.dismiss();
            }
        });

        removeAdDialog.show();
    }

    // for load reward ad
    private void loadRewardAd() {
        rewardedAd = new RewardedAd(this,
                getString(R.string.video_ad_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    // for checking if a day is completed
    private void aDayCompleted() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences dayPref = getApplicationContext().getSharedPreferences("dayPref",0);
        int lastDay = dayPref.getInt("day",0);

        if(lastDay != currentDay){
            SharedPreferences.Editor editor = dayPref.edit();
            editor.putInt("day",currentDay);
            editor.commit();
            // ----- show ad to true when a day completed -----
            SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
            SharedPreferences.Editor adeditor = pref.edit();
            adeditor.putBoolean("showAd",true);
            adeditor.commit();
        }else {
            Log.d(TAG, "aDayCompleted: " + "a day is not completed yet");
        }
    }
}