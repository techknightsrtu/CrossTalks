package com.techknightsrtu.crosstalks.app.feature.home.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.app.models.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserChatViewHolder extends RecyclerView.ViewHolder {

    public TextView tvUserName, tvStartChat;
    public ImageView ivUserAvatar, ivOnlineIndicator;
    public ShimmerFrameLayout svChatLoading;
    public RelativeLayout rlChatLoaded;

    private OnChatButtonClick onChatButtonClick;

    public UserChatViewHolder(@NonNull View itemView, final User model, final OnChatButtonClick onChatButtonClick) {
        super(itemView);

        this.onChatButtonClick = onChatButtonClick;

        ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvStartChat = itemView.findViewById(R.id.tvStartChat);

        ivOnlineIndicator = itemView.findViewById(R.id.ivOnlineIndicator);

        rlChatLoaded = itemView.findViewById(R.id.rlChatLoaded);

        svChatLoading = itemView.findViewById(R.id.svChatLoading);

        rlChatLoaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int avatarId = Avatar.nameList.indexOf(tvUserName.getText().toString());

                onChatButtonClick.onChatClick(avatarId,model.getUserId());

            }
        });

        tvStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int avatarId = Avatar.nameList.indexOf(tvUserName.getText().toString());

                onChatButtonClick.onChatClick(avatarId,model.getUserId());

            }
        });

    }

}
