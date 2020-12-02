package com.techknightsrtu.crosstalks.app.feature.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    private void setupOnlineUsers(){

        onlineChatAdapter = FirebaseMethods.setupOnlineChatsAdapter(prefs.getCollegeId(),UsersListFragment.this);
        rvOnlineUsers.setAdapter(onlineChatAdapter);
        onlineChatAdapter.notifyDataSetChanged();

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

        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("userId",userId);
        i.putExtra("avatarId",String.valueOf(avatarId));
        startActivity(i);

    }

    @Override
    public void onChatLongClick(String userId, String channelId, View v) {

    }

}