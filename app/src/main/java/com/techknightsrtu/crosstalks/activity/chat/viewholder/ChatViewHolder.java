package com.techknightsrtu.crosstalks.activity.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.helper.Avatar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvLastMessage, tvLastMessageTime, tvUnreadCount;
    public ImageView ivUserAvatar, ivOnlineIndicator;
    public ShimmerFrameLayout svChatLoading;
    public RelativeLayout rlChatLoaded;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
        tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
        ivOnlineIndicator = itemView.findViewById(R.id.ivOnlineIndicator);

        svChatLoading = itemView.findViewById(R.id.svChatLoading);
        rlChatLoaded = itemView.findViewById(R.id.rlChatLoaded);


    }
}
