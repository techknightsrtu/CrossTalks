package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;

public interface GetLastMessage {
    void onCallback(Message lastMessage);
}
