package com.techknightsrtu.crosstalks.activity.chat.onClickListeners;

import android.view.View;

public interface OnChatButtonClick {

    void onChatClick(int avatarId, String userId);

    void onChatLongClick(String userId, View v);

}
