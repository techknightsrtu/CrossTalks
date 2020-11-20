package com.techknightsrtu.crosstalks.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.ChatActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;

import java.util.List;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseCloudMessagingS";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null){

            Log.d(TAG, "onMessageReceived: FCM Message received" + remoteMessage.getData());

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            String avatarId = remoteMessage.getData().get("avatarId").toString();
            String userId = remoteMessage.getData().get("userId");

            Context mActivity = getApplicationContext();

            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, ChatActivity.class);
            resultIntent.putExtra("avatarId",avatarId);
            resultIntent.putExtra("userId",userId);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent
                    .getActivity(FirebaseCloudMessagingService.this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationManager mNotifyManager = (NotificationManager)
                    mActivity.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity, getString(R.string.chats_notification_channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(contentIntent)
                    .setColor(ContextCompat.getColor(mActivity, R.color.red))
                    .setContentTitle(title).setContentText(body);

            Notification n = mBuilder.build();

            n.defaults |= Notification.DEFAULT_ALL;
            mNotifyManager.notify(1, n);

            Log.d(TAG, "onMessageReceived: FOREGROUND NOTIFICATIONS WORKING");

        }

    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        if(FirebaseMethods.isUserSignedIn()){
            addTokenToFirebase(token);
        }

    }

    public static void addTokenToFirebase(final String newToken){

        if(newToken.equals("")){
            Log.d(TAG, "addTokenToFirebase: FCM TOKEN IS NULL" );
            return;
        }


        FirebaseMethods.getRegistrationTokens(new GetRegistrationToken() {
            @Override
            public void onCallback(List<String> tokens) {

                if(!tokens.isEmpty() && !tokens.contains(newToken)){
                    tokens.add(newToken);

                    FirebaseMethods.setRegistrationToken(tokens);

                }
            }
        });

    }
}
