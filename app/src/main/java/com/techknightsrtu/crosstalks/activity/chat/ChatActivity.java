package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.models.MessageType;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.DoesChatChannelExist;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserOnlineStatus;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.ProgressDialog;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    
    private String chatUserId;
    private String currUserId;
    private String chatUserAvatarId;
    private RecyclerView rvMessages;
    private LinearLayout llSafetyGuide;

    // Google banner ad
    private FrameLayout ad_view_container;

    // Progress Dialog
    private ProgressDialog progressDialog;

    private UserProfileDataPref prefs;

    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private AppCompatButton btSendMessage;
    private EditText etWriteMessage;
    private ImageView ivOnlineIndicator;

    private ValueEventListener chatSeenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        // For Loading Ads
        GoogleAdMob googleAdMob = new GoogleAdMob(ChatActivity.this, ad_view_container);
        googleAdMob.loadAd();

        setupToolbar();
        setupChatChannel();

    }

    private void init() {

        progressDialog = new ProgressDialog(ChatActivity.this);

        llSafetyGuide = findViewById(R.id.llSafetyGuide);

        prefs = new UserProfileDataPref(ChatActivity.this);

        currUserId = FirebaseMethods.getUserId();

        chatUserId = getIntent().getStringExtra("userId");
        chatUserAvatarId = getIntent().getStringExtra("avatarId");

        TextView tvChatUserName = findViewById(R.id.tvChatUserName);
        tvChatUserName.setText(Avatar.nameList.get(Integer.parseInt(chatUserAvatarId)));

        ImageView ivChatAvatar = findViewById(R.id.ivChatAvatar);
        ivChatAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(chatUserAvatarId)));

        etWriteMessage = findViewById(R.id.etWriteMessage);
        ivOnlineIndicator = findViewById(R.id.ivOnlineIndicator);

        btSendMessage = findViewById(R.id.btSendMessage);

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        ad_view_container = findViewById(R.id.ad_view_container);
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
        progressDialog.showProgressDialog();

        FirebaseMethods.getUserOnlineStatus(chatUserId, new GetUserOnlineStatus() {
            @Override
            public void onCallback(String status) {

                Log.d(TAG, "onBindViewHolder: " + status);

                if(status != null && status.equals("Online")){
                    ivOnlineIndicator.setVisibility(View.VISIBLE);
                }else{
                    ivOnlineIndicator.setVisibility(View.GONE);
                }
            }
        });

        btSendMessage.setOnClickListener(view -> {

            Animation animateButton = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.send_msg_button_anim);
            btSendMessage.startAnimation(animateButton);


            ChatMethods.checkAndGetChatChannelIfExist(currUserId, chatUserId, (exist, channelId) -> {

                if(!exist){
                    channelId = ChatMethods.createChatChannel(currUserId,chatUserId);
                }

                if(!etWriteMessage.getText().toString().trim().isEmpty()){

                    String senderAvatarName = Avatar.nameList.get(Integer.parseInt(prefs.getAvatarId()));
                    String senderAvatarId = prefs.getAvatarId();

                    String timestamp = Utility.getCurrentTimestamp();

                    ChatMethods.setChannelLastActiveStatus(timestamp,currUserId,chatUserId);

                    Message m = new Message(timestamp,
                            currUserId,senderAvatarName,senderAvatarId,
                            chatUserId,etWriteMessage.getText().toString().trim(),
                            MessageType.TEXT,false);

                    etWriteMessage.setText("");

                    ChatMethods.sendTextMessage(channelId,m);
                }

            });





        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        ChatMethods.checkAndGetChatChannelIfExist(currUserId, chatUserId,(exist, channelId) -> {

            progressDialog.hideProgressDialog();
            if(exist){

                llSafetyGuide.setVisibility(View.GONE);
                messagesAdapter = ChatMethods.setupFirebaseChatsAdapter(channelId);

                messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = messagesAdapter.getItemCount();
                        int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {

                            linearLayoutManager.scrollToPosition(positionStart);

                        }
                    }
                });

                rvMessages.setAdapter(messagesAdapter);

                messagesAdapter.startListening();

                chatSeenListener = ChatMethods.updateSeenMessage(channelId,currUserId,chatUserId);

            }else{
                   llSafetyGuide.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStop() {

        if(messagesAdapter != null)
            messagesAdapter.stopListening();

        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        FirebaseMethods.setUserOnlineStatus("Offline");

        ChatMethods.checkAndGetChatChannelIfExist(currUserId, chatUserId, (exist, channelId) -> {
            if(exist){
                ChatMethods.removeChatSeenListener(channelId,chatSeenListener);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus("Online");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}