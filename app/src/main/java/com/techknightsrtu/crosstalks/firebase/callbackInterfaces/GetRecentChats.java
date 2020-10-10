package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import java.util.ArrayList;
import java.util.Map;

public interface GetRecentChats {
    void onCallback(ArrayList<Map<String,String>> recentChatsList);
}
