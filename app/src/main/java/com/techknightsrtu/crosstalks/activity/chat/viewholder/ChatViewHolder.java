package com.techknightsrtu.crosstalks.activity.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvLastMessage, tvLastMessageTime, tvUnreadCount;
    public ImageView ivUserAvatar;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
        tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
    }
}
