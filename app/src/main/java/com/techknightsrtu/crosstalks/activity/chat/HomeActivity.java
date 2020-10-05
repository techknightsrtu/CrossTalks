package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.activity.chat.adapter.ChatAdapter;
import com.techknightsrtu.crosstalks.activity.chat.adapter.StoriesAdapter;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";


    // Widgets
    private RecyclerView rvAnonymousStories, rvChats;

    // Adapter
    private StoriesAdapter storiesAdapter;
    private ChatAdapter chatAdapter;

    //LocalData
    UserProfileDataPref prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setupBottomNavigationBar();

        storiesAdapter = new StoriesAdapter(HomeActivity.this);
        rvAnonymousStories.setAdapter(storiesAdapter);

        chatAdapter = new ChatAdapter(HomeActivity.this);
        rvChats.setAdapter(chatAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus(true,prefs.getCollegeId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseMethods.setUserOnlineStatus(false,prefs.getCollegeId());
    }


    private void init(){

        prefs = new UserProfileDataPref(HomeActivity.this);

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCollegeName.setText(prefs.getCollegeName());

        rvAnonymousStories = findViewById(R.id.rvAnonymousStories);
        rvChats = findViewById(R.id.rvChats);

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

    }

}