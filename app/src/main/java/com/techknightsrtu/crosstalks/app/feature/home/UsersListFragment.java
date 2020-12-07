package com.techknightsrtu.crosstalks.app.feature.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.BuildConfig;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.ChatActivity;
import com.techknightsrtu.crosstalks.app.feature.home.adapter.UserChatAdapter;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersListFragment extends Fragment implements OnChatButtonClick {

    private static final String TAG = "UsersListFragment";
    
    //Widgets
    private RecyclerView rvOnlineUsers;
    private View mView;
    private TextView tvShareApp;
    private LinearLayout llEmpty;

    //Adapter
    private UserChatAdapter onlineChatAdapter;

    //LocalData
    UserProfileDataPref prefs;

    public UsersListFragment() {
        // Required empty public constructor
    }

    public static UsersListFragment newInstance() {
        return new UsersListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_users_list, container, false);

        init();

        return mView;
    }

    private void init(){

        prefs = new UserProfileDataPref(getActivity());

        rvOnlineUsers = mView.findViewById(R.id.rvOnlineUsers);

        tvShareApp = mView.findViewById(R.id.tvShareApp);

        llEmpty = mView.findViewById(R.id.llEmpty);

        tvShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossTalks");
                    String shareMessage= "\nBored in this Quarantine ??? Let's have some fun, Download CrossTalks and chat with your mates anonymously.   \n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Invite your friends"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

    }

    private void setupOnlineUsers(){

        onlineChatAdapter = FirebaseMethods.setupOnlineChatsAdapter(prefs.getCollegeId(),
                UsersListFragment.this, llEmpty);

        rvOnlineUsers.setAdapter(onlineChatAdapter);

        onlineChatAdapter.onDataChanged();


        if(onlineChatAdapter.getItemCount() == 0){
            rvOnlineUsers.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }else {
            llEmpty.setVisibility(View.VISIBLE);
            rvOnlineUsers.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        setupOnlineUsers();
        onlineChatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        onlineChatAdapter.stopListening();
    }

    @Override
    public void onChatClick(int avatarId, String userId) {

        Log.d(TAG, "onChatClick: " + avatarId + " " + userId);


        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("userId",userId);
        i.putExtra("avatarId",String.valueOf(avatarId));
        startActivity(i);

    }

    @Override
    public void onChatLongClick(String userId, String channelId, View v) {

    }

}