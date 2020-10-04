package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.helper.Avatar;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String PREFS_NAME = "AppLocalData";
    private int avatarId;
    private String collegeName, collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getDataFromLocalCache();
        init();
        setupBottomNavigationBar();

    }

    private void getDataFromLocalCache(){

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        avatarId = Integer.parseInt(prefs.getString("avatarId",""));
        collegeName = prefs.getString("collegeName","noCollege");
        collegeId = prefs.getString("collegeId","nc");

    }

    private void init(){

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        if (!collegeName.equals("noCollege")){
            tvCollegeName.setText(collegeName);
        }

    }

    private void setupBottomNavigationBar(){

        ImageView chatLabel = findViewById(R.id.ivChatIcon);
        ImageView onlineLabel = findViewById(R.id.ivOnlineIcon);
        ImageView profileLabel = findViewById(R.id.ivProfileIcon);

        chatLabel.setImageResource(R.drawable.ic_chats_selected);

        onlineLabel.setImageResource(R.drawable.ic_online_not_selected);
        onlineLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this,OnlineUsersActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        profileLabel.setImageResource(Avatar.avatarList.get(avatarId));

        profileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

}