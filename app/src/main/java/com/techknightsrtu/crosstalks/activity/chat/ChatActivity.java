package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.models.MessageType;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetMessagesFromChannel;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.Utility;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private String chatUserId;
    private String currUserId;
    private int chatUserAvatarId;
    private RecyclerView rvMessages;

    private MessagesAdapter messagesAdapter;
    private AppCompatButton btSendMessage;
    private EditText etWriteMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        setupToolbar();
        setupChatChannel();

    }

    private void init() {

        currUserId = FirebaseMethods.getUserId();

        chatUserId = getIntent().getStringExtra("userId");
        chatUserAvatarId = getIntent().getIntExtra("avatarId",0);

        TextView tvChatUserName = findViewById(R.id.tvChatUserName);
        tvChatUserName.setText(Avatar.nameList.get(chatUserAvatarId));

        ImageView ivChatAvatar = findViewById(R.id.ivChatAvatar);
        ivChatAvatar.setImageResource(Avatar.avatarList.get(chatUserAvatarId));


        etWriteMessage = findViewById(R.id.etWriteMessage);

        btSendMessage = findViewById(R.id.btSendMessage);

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(ChatActivity.this,R.color.bg_fill));
    }

    private void setupChatChannel(){

        ChatMethods.getOrCreateChatChannel(currUserId, chatUserId, new GetChatChannel() {
            @Override
            public void onCallback(final String channelId) {

                ChatMethods.getMessages(channelId, new GetMessagesFromChannel() {
                    @Override
                    public void onCallback(ArrayList<Message> list) {

                        messagesAdapter = new MessagesAdapter(ChatActivity.this,list);
                        rvMessages.setAdapter(messagesAdapter);

                    }
                });

                btSendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Animation animateButton = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.send_msg_button_anim);
                        btSendMessage.startAnimation(animateButton);

                        if(!etWriteMessage.getText().toString().trim().isEmpty()){
                            Message m = new Message(Utility.getCurrentTimestamp(),
                                    currUserId, chatUserId,etWriteMessage.getText().toString().trim(),
                                    MessageType.TEXT);

                            etWriteMessage.setText("");

                            ChatMethods.sendTextMessage(channelId,m);
                        }

                        rvMessages.scrollToPosition(messagesAdapter.getItemCount()-1);
                    }
                });

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}