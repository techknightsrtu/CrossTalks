package com.techknightsrtu.crosstalks;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.FirebaseDatabase;

public class CrossTalks extends Application {

    private static final String TAG = "CrossTalks";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Log.d(TAG, "onCreate: APPLICATION IS RUNNING");

        NotificationManager mNotifyManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel(mNotifyManager);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel( NotificationManager notificationManager){

        String id  = getString(R.string.chats_notification_channel_id);
        String name = getString(R.string.chats_notification_channel_name);
        String description = "This channel sends notifications related to chat conversations";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.setVibrationPattern(new long[] { 0, 100, 50});
        mChannel.enableLights(true);
        notificationManager.createNotificationChannel(mChannel);

    }



}
