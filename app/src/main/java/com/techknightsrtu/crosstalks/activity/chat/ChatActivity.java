package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;

public class ChatActivity extends AppCompatActivity {

    private String chatUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatUserId = getIntent().getStringExtra("userId");

        Toast.makeText(this, "Chat with " + chatUserId, Toast.LENGTH_LONG).show();
    }


}