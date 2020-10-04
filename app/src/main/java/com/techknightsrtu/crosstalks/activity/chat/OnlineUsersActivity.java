package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.FirebaseMethods;

import org.w3c.dom.Text;

public class OnlineUsersActivity extends AppCompatActivity {

    //Values
    private int avatarId;
    private String collegeName, collegeId;
    private static final String PREFS_NAME = "AppLocalData";

    //Widgets
    RecyclerView rvOnlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

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

        rvOnlineUsers = findViewById(R.id.rvOnlineUsers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus(true,collegeId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseMethods.setUserOnlineStatus(false,collegeId);
    }

    private void setupBottomNavigationBar(){

        ImageView chatLabel = findViewById(R.id.ivChatIcon);
        ImageView onlineLabel = findViewById(R.id.ivOnlineIcon);
        ImageView profileLabel = findViewById(R.id.ivProfileIcon);

        chatLabel.setImageResource(R.drawable.ic_chats_not_selected);
        chatLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OnlineUsersActivity.this,HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        onlineLabel.setImageResource(R.drawable.ic_online_selected);

        profileLabel.setImageResource(Avatar.avatarList.get(avatarId));

        profileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OnlineUsersActivity.this, ProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }



}