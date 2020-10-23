package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.ChatViewHolder;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetLastMessage;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.models.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private Activity activity;
    private ArrayList<Map<String,String>> recentChatsList;

    private OnChatButtonClick onChatButtonClick;

    public ChatAdapter(Activity activity, ArrayList<Map<String,String>> recentChatsList, OnChatButtonClick onChatButtonClick) {
        this.activity = activity;
        this.recentChatsList = recentChatsList;
        this.onChatButtonClick = onChatButtonClick;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(view,recentChatsList,onChatButtonClick);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {

       Map<String,String> m = recentChatsList.get(position);

       String userId = m.get("userId").toString();
       String channelId = m.get("channelId").toString();

        FirebaseMethods.getOnlyUserData(userId, new GetOnlyUserData() {
            @Override
            public void onCallback(User user) {
                holder.ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(user.getAvatarId())));
                holder.tvUserName.setText(Avatar.nameList.get(Integer.parseInt(user.getAvatarId())));
                holder.svChatLoading.setVisibility(View.GONE);
                holder.rlChatLoaded.setVisibility(View.VISIBLE);
            }
        });

        ChatMethods.getLastMessage(channelId, new GetLastMessage() {
            @Override
            public void onCallback(Message lastMessage) {

                if(lastMessage != null){
                    holder.tvLastMessage.setText(lastMessage.getMessage());
                    holder.tvLastMessageTime.setText(Utility.getTimeFromTimestamp(lastMessage.getTimestamp()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return recentChatsList.size();
    }

}
