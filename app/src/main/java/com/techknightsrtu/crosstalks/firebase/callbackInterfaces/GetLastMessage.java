package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.activity.chat.models.Message;

import java.util.ArrayList;

public interface GetLastMessage {
    void onCallback(Message lastMessage);
}
