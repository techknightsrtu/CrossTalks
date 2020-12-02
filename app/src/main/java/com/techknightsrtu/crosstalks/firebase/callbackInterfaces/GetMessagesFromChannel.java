package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;

import java.util.ArrayList;

public interface GetMessagesFromChannel {

    void onCallback(ArrayList<Message> list);

}
