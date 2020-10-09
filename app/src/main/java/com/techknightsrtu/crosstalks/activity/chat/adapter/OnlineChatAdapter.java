package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.models.User;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.OnlineChatViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OnlineChatAdapter extends RecyclerView.Adapter<OnlineChatViewHolder> {

    private Activity activity;

    private ArrayList<String> onlineUsersList;

    private OnChatButtonClick onChatButtonClick;

    public OnlineChatAdapter(Activity activity, ArrayList<String> onlineUsersList, OnChatButtonClick onChatButtonClick ) {
        this.activity = activity;
        this.onlineUsersList = onlineUsersList;
        this.onChatButtonClick = onChatButtonClick;
    }

    @NonNull
    @Override
    public OnlineChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.online_chat_item, parent, false);

        return new OnlineChatViewHolder(view,onlineUsersList,onChatButtonClick);
    }

    @Override
    public void onBindViewHolder(@NonNull final OnlineChatViewHolder holder, int position) {

        String userId = onlineUsersList.get(position);

        FirebaseMethods.getOnlyUserData(userId, new GetOnlyUserData() {
            @Override
            public void onCallback(User user) {

                holder.ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(user.getAvatarId())));
                holder.tvUserName.setText(Avatar.nameList.get(Integer.parseInt(user.getAvatarId())));

            }
        });

    }

    @Override
    public int getItemCount() {
        return onlineUsersList.size();
    }


}
