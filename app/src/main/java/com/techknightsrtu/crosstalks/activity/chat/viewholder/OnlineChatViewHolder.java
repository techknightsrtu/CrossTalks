package com.techknightsrtu.crosstalks.activity.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.helper.Avatar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OnlineChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvStartChat;
    public ImageView ivUserAvatar;
    public ShimmerFrameLayout svChatLoading;
    public RelativeLayout rlChatLoaded;

    private ArrayList<String> onlineUsersList;

    private OnChatButtonClick onChatButtonClick;

    public OnlineChatViewHolder(@NonNull View itemView, final ArrayList<String> onlineUsersList, final OnChatButtonClick onChatButtonClick) {
        super(itemView);

        this.onChatButtonClick = onChatButtonClick;
        this.onlineUsersList = onlineUsersList;

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvStartChat = itemView.findViewById(R.id.tvStartChat);

        rlChatLoaded = itemView.findViewById(R.id.rlChatLoaded);

        svChatLoading = itemView.findViewById(R.id.svChatLoading);

        tvStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int avatarId = Avatar.nameList.indexOf(tvUserName.getText().toString());

                onChatButtonClick.onChatClick(avatarId, onlineUsersList.get(getAdapterPosition()));

            }
        });

    }

}
