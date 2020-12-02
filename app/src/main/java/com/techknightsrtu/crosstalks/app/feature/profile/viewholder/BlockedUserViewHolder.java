package com.techknightsrtu.crosstalks.app.feature.profile.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlockedUserViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvUnblockChat, tvTimestamp;
    public ImageView ivUserAvatar;
    public ShimmerFrameLayout svChatLoading;
    public RelativeLayout rlChatLoaded;

    public BlockedUserViewHolder(@NonNull View itemView) {
        super(itemView);

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvUserName = itemView.findViewById(R.id.tvTimeStamp);
        tvUnblockChat = itemView.findViewById(R.id.tvUnblockChat);

        rlChatLoaded = itemView.findViewById(R.id.rlChatLoaded);

        svChatLoading = itemView.findViewById(R.id.svChatLoading);

        tvUnblockChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
