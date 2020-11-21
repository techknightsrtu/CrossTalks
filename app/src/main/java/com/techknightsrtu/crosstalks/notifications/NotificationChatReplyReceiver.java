package com.techknightsrtu.crosstalks.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

public class NotificationChatReplyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if(remoteInput!= null){
            CharSequence replyText =  remoteInput.getCharSequence("key_text_reply");
        }

    }
}
