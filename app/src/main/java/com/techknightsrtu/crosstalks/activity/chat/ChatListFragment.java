package com.techknightsrtu.crosstalks.activity.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.adapter.ChatAdapter;
import com.techknightsrtu.crosstalks.activity.chat.adapter.StoriesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRecentChats;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment implements OnChatButtonClick {


    // Widgets
    private RecyclerView rvAnonymousStories, rvChats;
    private View mView;

    // Adapter
    private StoriesAdapter storiesAdapter;
    private ChatAdapter chatAdapter;


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
        setupRecentChats();

        return mView;
    }


    private void init(){

        //rvAnonymousStories = mView.findViewById(R.id.rvAnonymousStories);
//        storiesAdapter = new StoriesAdapter(getActivity());
//        rvAnonymousStories.setAdapter(storiesAdapter);

        prefs = new UserProfileDataPref(getActivity());
        rvChats = mView.findViewById(R.id.rvChats);


    }

    private void setupRecentChats(){

        ChatMethods.getRecentChats(prefs.getUserId(), new GetRecentChats() {
            @Override
            public void onCallback(ArrayList<Map<String, String>> recentChatsList) {

                chatAdapter = new ChatAdapter(getActivity(),recentChatsList,ChatListFragment.this);
                rvChats.setAdapter(chatAdapter);

            }
        });

    }


    @Override
    public void onChatClick(int avatarId, String userId) {

        Intent i = new Intent(getContext(),ChatActivity.class);
        i.putExtra("userId",userId);
        i.putExtra("avatarId",String.valueOf(avatarId));
        startActivity(i);

    }
}