package com.techknightsrtu.crosstalks.activity.chat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
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
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.SplashActivity;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MyFragmentPagerAdapter;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.activity.chat.adapter.ChatAdapter;
import com.techknightsrtu.crosstalks.activity.chat.adapter.StoriesAdapter;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    //LocalData
    UserProfileDataPref prefs;

    //Widgets
    ViewPager viewPager;

    // Google banner ad
    private FrameLayout ad_view_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        init();
        setupBottomNavigationBar();

        // For Loading Ads
        GoogleAdMob googleAdMob = new GoogleAdMob(HomeActivity.this, ad_view_container);
        googleAdMob.loadAd();

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
        super.onPause();
        FirebaseMethods.setUserOnlineStatus("Offline");
    }

}