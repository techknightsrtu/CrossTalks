package com.techknightsrtu.crosstalks.app.feature.chat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;
import com.techknightsrtu.crosstalks.app.feature.chat.models.MessageType;
import com.techknightsrtu.crosstalks.app.feature.home.ChatListFragment;
import com.techknightsrtu.crosstalks.app.feature.home.HomeActivity;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.IsChatDeleted;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.app.helper.ProgressDialog;
import com.techknightsrtu.crosstalks.app.helper.Utility;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    public static boolean isVisible = false;

    private String chatUserId;
    private String currUserId;
    private String chatUserAvatarId;
    private RecyclerView rvMessages;
    private LinearLayout llSafetyGuide, llTypingIndicator, llDialogMessage, llSendMessage;

    // Google banner ad
    private FrameLayout ad_view_container;

    // Progress Dialog
    private ProgressDialog progressDialog;

    private UserProfileDataPref prefs;

    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private AppCompatButton btSendMessage;
    private EditText etWriteMessage;
    private TextView tvMessage;
    private ImageView ivOnlineIndicator;

    private ValueEventListener chatSeenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        // For Loading Ads
        SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
        if (pref.getBoolean("showAd", false)) {
            GoogleAdMob googleAdMob = new GoogleAdMob(ChatActivity.this, ad_view_container);
            googleAdMob.loadAd();
        }

        setupToolbar();

    }

    private void init() {

        progressDialog = new ProgressDialog(ChatActivity.this);

        llSafetyGuide = findViewById(R.id.llSafetyGuide);
        llTypingIndicator = findViewById(R.id.llTypingIndicator);
        llDialogMessage = findViewById(R.id.llDialogMessage);
        llSendMessage = findViewById(R.id.llSendMessage);

        prefs = new UserProfileDataPref(ChatActivity.this);

        currUserId = FirebaseMethods.getUserId();

        chatUserId = getIntent().getStringExtra("userId");
        chatUserAvatarId = getIntent().getStringExtra("avatarId");

        TextView tvChatUserName = findViewById(R.id.tvChatUserName);
        tvChatUserName.setText(Avatar.nameList.get(Integer.parseInt(chatUserAvatarId)));

        ImageView ivChatAvatar = findViewById(R.id.ivChatAvatar);
        ivChatAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(chatUserAvatarId)));

        etWriteMessage = findViewById(R.id.etWriteMessage);
        tvMessage = findViewById(R.id.tvMessage);
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

    private void checkChatChannelBlockAndDeleteStatus(){

        //Check if Current User Blocked chat User
        FirebaseMethods.isUserBlocked(currUserId,chatUserId,isBlocked -> {
            // TODO: Show Dialog Box
            if(isBlocked){
                if(!((Activity) ChatActivity.this).isFinishing())
                {
                    LayoutInflater factory = LayoutInflater.from(ChatActivity.this);
                    final View blockedUserDialogView = factory.inflate(R.layout.blocked_user_dialog, null);
                    final AlertDialog blockedUserDialog = new AlertDialog.Builder(ChatActivity.this).create();
                    blockedUserDialog.setView(blockedUserDialogView);

                    Window window = blockedUserDialog.getWindow();
                    window.setBackgroundDrawableResource(android.R.color.transparent);

                    blockedUserDialogView.findViewById(R.id.tvUnblockChat).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseMethods.unblockThisUser(chatUserId);
                            blockedUserDialog.dismiss();
                        }
                    });

                    blockedUserDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                             KeyEvent event) {
                            // TODO Auto-generated method stub
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                finish();
                            }
                            return true;
                        }
                    });

                    blockedUserDialog.show();
                    blockedUserDialog.setCancelable(false);
                }

            }
        });

        //Check if Chat User Blocked Current User
        FirebaseMethods.isUserBlocked(chatUserId,currUserId,isBlocked -> {
            if(isBlocked){
                llSendMessage.setVisibility(View.GONE);
                llDialogMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(R.string.label_user_blocked_msg);
            }else{
                llSendMessage.setVisibility(View.VISIBLE);
                llDialogMessage.setVisibility(View.GONE);
            }
        });

    }

    private void setupChatChannel(String channelId){

        progressDialog.showProgressDialog();

        ChatMethods.checkIfChatUserDeletedChat(channelId, currUserId, new IsChatDeleted() {
            @Override
            public void onCallback(boolean isDeleted) {
                // TODO: Handle if chat User Deleted Chat
                if(isDeleted){
                    llSendMessage.setVisibility(View.GONE);
                    llDialogMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(R.string.label_user_chat_deleted_msg);
                }
            }
        });

        FirebaseMethods.getUserOnlineStatus(chatUserId, (status) -> {

            Log.d(TAG, "onBindViewHolder: " + status);

            if(status != null && status.equals("Online")){
                ivOnlineIndicator.setVisibility(View.VISIBLE);
            }else{
                ivOnlineIndicator.setVisibility(View.GONE);
            }

        });

        Log.d(TAG, "onCallback: THIS IS CHAT CHANNEL" + channelId);

        ChatMethods.getUserTypingStatus(channelId, chatUserId, typingStatus -> {

            if(typingStatus){
                llTypingIndicator.setVisibility(View.VISIBLE);
            }else{
                llTypingIndicator.setVisibility(View.GONE);
            }

        });

        etWriteMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ChatMethods.setUserTypingStatus(channelId,charSequence.length() > 0);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Animation animateButton = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.send_msg_button_anim);

        btSendMessage.setOnClickListener(view -> {

            btSendMessage.startAnimation(animateButton);

            if(!etWriteMessage.getText().toString().trim().isEmpty()){

                new Thread(() -> {

                    String senderAvatarName = Avatar.nameList.get(Integer.parseInt(prefs.getAvatarId()));
                    String senderAvatarId = prefs.getAvatarId();

                    String timestamp = Utility.getCurrentTimestamp();

                    ChatMethods.setChannelLastActiveStatus(timestamp,currUserId,chatUserId);

                    Message m = new Message(timestamp,
                            currUserId,senderAvatarName,senderAvatarId,
                            chatUserId,etWriteMessage.getText().toString().trim(),
                            MessageType.TEXT,false);

                    ChatMethods.sendTextMessage(channelId,m);

                }).start();

                etWriteMessage.setText("");
            }

        });

        progressDialog.hideProgressDialog();
    }

    private void getChatChannelAndSetup(){

        ChatMethods.getOrCreateChatChannel(currUserId, chatUserId, new GetChatChannel() {
            @Override
            public void onCallback(String channelId) {

                setupChatChannel(channelId);

                messagesAdapter = ChatMethods.setupFirebaseChatsAdapter(channelId);

                if(messagesAdapter.getItemCount() == 0){
                    llSafetyGuide.setVisibility(View.GONE);
                }else{
                    llSafetyGuide.setVisibility(View.VISIBLE);
                }

                rvMessages.setAdapter(messagesAdapter);

                messagesAdapter.startListening();

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

                chatSeenListener = ChatMethods.updateSeenMessage(channelId,currUserId,chatUserId);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkChatChannelBlockAndDeleteStatus();

        getChatChannelAndSetup();

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
        messagesAdapter.stopListening();
        isVisible = false;
        super.onStop();
    }

    @Override
    public void onPause() {
        isVisible = false;
        FirebaseMethods.setUserOnlineStatus("Offline");

        ChatMethods.getOrCreateChatChannel(currUserId, chatUserId, channelId -> {
            ChatMethods.removeChatSeenListener(channelId,chatSeenListener);
            ChatMethods.setUserTypingStatus(channelId,false);
        });

        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        FirebaseMethods.setUserOnlineStatus("Online");
    }

    @Override
    public void onDestroy() {
        isVisible = false;
        super.onDestroy();

       // ChatMethods.deleteChatChannelIfNoChat(currUserId, chatUserId);
    }

}