package com.techknightsrtu.crosstalks.app.feature.home.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecentChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvLastMessage, tvLastMessageTime, tvUnreadCount;
    public ImageView ivUserAvatar, ivOnlineIndicator;
    public ShimmerFrameLayout svChatLoading;
    public RelativeLayout rlChatLoaded;

    public RecentChatViewHolder(@NonNull View itemView) {
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
