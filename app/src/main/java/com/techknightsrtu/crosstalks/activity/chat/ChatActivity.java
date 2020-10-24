package com.techknightsrtu.crosstalks.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.auth.ChooseCollegeActivity;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.models.MessageType;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetMessagesFromChannel;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.ProgressDialog;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    
    private String chatUserId;
    private String currUserId;
    private String chatUserAvatarId;
    private RecyclerView rvMessages;
    private LinearLayout llSafetyGuide;

    // Google banner ad
    private FrameLayout ad_view_container;
    private AdView adView;

    // Progress Dialog
    private ProgressDialog progressDialog;

    private UserProfileDataPref prefs;

    private MessagesAdapter messagesAdapter;
    private AppCompatButton btSendMessage;
    private EditText etWriteMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        loadAd();
        setupToolbar();
        setupChatChannel();

    }

    private void loadAd() {
        ad_view_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });
    }

    private void loadBanner() {
        // Create an ad request.
        adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.AD_UNIT_ID));
        ad_view_container.removeAllViews();
        ad_view_container.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = ad_view_container.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(this, adWidth);
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

        btSendMessage = findViewById(R.id.btSendMessage);

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
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

        ChatMethods.getOrCreateChatChannel(currUserId, chatUserId, new GetChatChannel() {
            @Override
            public void onCallback(final String channelId) {

                ChatMethods.getMessages(channelId, new GetMessagesFromChannel() {
                    @Override
                    public void onCallback(ArrayList<Message> list) {
                        progressDialog.hideProgressDialog();

                        if(!list.isEmpty()){
                            llSafetyGuide.setVisibility(View.GONE);
                        }else{
                            llSafetyGuide.setVisibility(View.VISIBLE);
                        }

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

                            String senderAvatarName = Avatar.nameList.get(Integer.parseInt(prefs.getAvatarId()));
                            String senderAvatarId = prefs.getAvatarId();

                            String timestamp = Utility.getCurrentTimestamp();

                            ChatMethods.setChannelLastActiveStatus(timestamp,currUserId,chatUserId);

                            Message m = new Message(timestamp,
                                    currUserId,senderAvatarName,senderAvatarId,
                                    chatUserId,etWriteMessage.getText().toString().trim(),
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

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}