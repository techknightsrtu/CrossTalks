package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.profile.ProfileActivity;

public class OnlineUsersActivity extends AppCompatActivity {

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    //Widgets
    RecyclerView rvOnlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvOnlineUsers = findViewById(R.id.rvOnlineUsers);

        setupBottomNavigationBar();
        
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