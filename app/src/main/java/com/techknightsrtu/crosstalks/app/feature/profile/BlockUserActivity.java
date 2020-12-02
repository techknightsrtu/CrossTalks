package com.techknightsrtu.crosstalks.app.feature.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.ChatActivity;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.app.feature.profile.adapter.BlockedUserAdapter;
import com.techknightsrtu.crosstalks.app.feature.profile.interfaces.OnUnblockButtonClick;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;

public class BlockUserActivity extends AppCompatActivity implements OnUnblockButtonClick {

    private static final String TAG = "BlockUserActivity";

    private LinearLayout llEmpty;
    private RecyclerView rvBlockedChats;
    private Button btBack;

    private BlockedUserAdapter blockedUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user);

        init();
        setupToolbar();


    }

    private void init() {

        llEmpty = findViewById(R.id.llEmpty);

        btBack = findViewById(R.id.btBack);

        rvBlockedChats = findViewById(R.id.rvBlockedChats);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(BlockUserActivity.this,R.color.bg_fill));
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

    private void setupBlockList(){

        blockedUserAdapter = FirebaseMethods
                .setupUserBlockListAdapter(BlockUserActivity.this,llEmpty);

        rvBlockedChats.setAdapter(blockedUserAdapter);

        blockedUserAdapter.onDataChanged();

        if(blockedUserAdapter.getItemCount() == 0){
            rvBlockedChats.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }else {
            llEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        setupBlockList();
        blockedUserAdapter.startListening();
    }

    @Override
    protected void onPause() {
        FirebaseMethods.setUserOnlineStatus("Offline");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus("Online");
    }

    @Override
    protected void onStop() {
        blockedUserAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onUnblockClick(int avatarId, String userId) {

        FirebaseMethods.unblockThisUser(userId);

        Toast.makeText(this, "User Unblocked", Toast.LENGTH_SHORT).show();

    }

}