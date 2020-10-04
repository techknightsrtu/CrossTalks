package com.techknightsrtu.crosstalks.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvStartChat;
    public ImageView ivUserAvatar;

    public OnlineChatViewHolder(@NonNull View itemView) {
        super(itemView);

        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvStartChat = itemView.findViewById(R.id.tvStartChat);

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
    }
}
