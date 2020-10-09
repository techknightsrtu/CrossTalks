package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.activity.chat.adapter.OnlineChatAdapter;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.OnlineUsersFromCollege;

import java.util.ArrayList;

public class OnlineUsersActivity extends AppCompatActivity implements OnChatButtonClick {


    //Widgets
    private RecyclerView rvOnlineUsers;

    //Adapter
    private OnlineChatAdapter onlineChatAdapter;

    //LocalData
    UserProfileDataPref prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

        init();
        setupBottomNavigationBar();
        setupOnlineUsers();

    }


    private void init(){

        prefs = new UserProfileDataPref(OnlineUsersActivity.this);

        TextView tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCollegeName.setText(prefs.getCollegeName());

        rvOnlineUsers = findViewById(R.id.rvOnlineUsers);

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

        profileLabel.setImageResource(Avatar.avatarList.get(Integer.parseInt(prefs.getAvatarId())));

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


    private void setupOnlineUsers(){

        FirebaseMethods.getOnlineUserFromCollege(prefs.getCollegeId(), new OnlineUsersFromCollege() {
            @Override
            public void onCallback(ArrayList<String> onlineUsersList) {

                onlineChatAdapter = new OnlineChatAdapter(OnlineUsersActivity.this,onlineUsersList,OnlineUsersActivity.this);
                rvOnlineUsers.setAdapter(onlineChatAdapter);

                onlineChatAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onChatClick(String userId) {

        Toast.makeText(this, "Chat button clicked : " + userId, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(OnlineUsersActivity.this,ChatActivity.class);
        i.putExtra("userId",userId);
        startActivity(i);
    }
}