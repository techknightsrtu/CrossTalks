package com.techknightsrtu.crosstalks.activity.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.helper.Avatar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvLastMessage, tvLastMessageTime, tvUnreadCount;
    public ImageView ivUserAvatar;

    private ArrayList<Map<String,String>> recentChatsList;

    private OnChatButtonClick onChatButtonClick;

    public ChatViewHolder(@NonNull View itemView,
                          final ArrayList<Map<String,String>> recentChatsList, final OnChatButtonClick onChatButtonClick) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int avatarId = Avatar.nameList.indexOf(tvUserName.getText().toString());

                Map<String,String> m = recentChatsList.get(getAdapterPosition());
                String userId = m.get("userId").toString();

                onChatButtonClick.onChatClick(avatarId,userId);

            }
        });

        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
        tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
    }
}
