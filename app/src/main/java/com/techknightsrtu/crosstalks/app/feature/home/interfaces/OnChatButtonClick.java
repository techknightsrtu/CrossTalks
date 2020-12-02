package com.techknightsrtu.crosstalks.app.feature.home.interfaces;

import android.view.View;

public interface OnChatButtonClick {

    void onChatClick(int avatarId, String userId);

    void onChatLongClick(String userId, String channelId, View v);

}
