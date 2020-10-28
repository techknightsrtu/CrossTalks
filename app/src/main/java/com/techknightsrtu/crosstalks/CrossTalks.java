package com.techknightsrtu.crosstalks;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.FirebaseDatabase;

public class CrossTalks extends Application {

    private static final String TAG = "CrossTalks";

    public static final String CHANNEL_CHAT_NOTIFICATIONS = "channel_chat_notifications";

    @Override
    public void onCreate() {
        super.onCreate();



        Log.d(TAG, "onCreate: APPLICATION IS RUNNING");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel( ){

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel chatNotificationChannel = new NotificationChannel(CHANNEL_CHAT_NOTIFICATIONS,
                "Chats Channel", NotificationManager.IMPORTANCE_HIGH);

        chatNotificationChannel.setDescription("This channel sends notifications related to chat conversations");

        manager.createNotificationChannel(chatNotificationChannel);
    }



}
