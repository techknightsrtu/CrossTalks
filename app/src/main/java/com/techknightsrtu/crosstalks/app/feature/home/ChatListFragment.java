package com.techknightsrtu.crosstalks.app.feature.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.ChatActivity;
import com.techknightsrtu.crosstalks.app.feature.home.adapter.RecentChatAdapter;
import com.techknightsrtu.crosstalks.app.feature.home.adapter.StoriesAdapter;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment implements OnChatButtonClick {


    // Widgets
    private RecyclerView rvAnonymousStories, rvChats;
    private View mView;
    private LinearLayout llEmpty;

    // Adapter
    private StoriesAdapter storiesAdapter;
    private RecentChatAdapter chatAdapter;


    //LocalData
    UserProfileDataPref prefs;


    public ChatListFragment() {
        // Required empty public constructor
    }

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_chat_list, container, false);
        init();

        return mView;
    }


    private void init(){

        //rvAnonymousStories = mView.findViewById(R.id.rvAnonymousStories);
//        storiesAdapter = new StoriesAdapter(getActivity());
//        rvAnonymousStories.setAdapter(storiesAdapter);

        prefs = new UserProfileDataPref(getActivity());
        rvChats = mView.findViewById(R.id.rvChats);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rvChats.setLayoutManager(mLayoutManager);

        llEmpty = mView.findViewById(R.id.llEmpty);

        rvChats.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);


    }

    private void setupRecentChats(){

        chatAdapter = ChatMethods.setupFirebaseRecentChatsAdapter(prefs.getUserId(),
                ChatListFragment.this,llEmpty);

        rvChats.setAdapter(chatAdapter);

        chatAdapter.onDataChanged();

        if(chatAdapter.getItemCount() == 0){
            rvChats.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }else {
            llEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

       setupRecentChats();
       chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        chatAdapter.stopListening();
        super.onStop();
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

        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);

        PopupMenu recentChatMenu = new PopupMenu(wrapper, v);

        recentChatMenu.getMenuInflater().inflate(R.menu.recent_chat_item_poupup_menu, recentChatMenu.getMenu());

        recentChatMenu.setOnMenuItemClickListener(menuItem -> {

            switch (menuItem.getItemId()){

                case R.id.deleteChat:

                    ChatMethods.removeCurrentUserFromChatChannel(channelId,userId);
                    return true;

                case R.id.blockChat:
                    FirebaseMethods.blockThisUser(userId);
                    Toast.makeText(wrapper, "User Blocked", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.reportChat:
                    // TODO: write code for reporting a user here
                    return true;
            }

            return false;
        });

        recentChatMenu.show();

    }




}