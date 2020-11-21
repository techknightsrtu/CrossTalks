package com.techknightsrtu.crosstalks.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.ChatActivity;

public class NotificationHelper {

    public static void handleChatNotification(Context context,
                                              String title,
                                              String body,
                                              String avatarId,
                                              String userId){

        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, ChatActivity.class);
        resultIntent.putExtra("avatarId",avatarId);
        resultIntent.putExtra("userId",userId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent
                .getActivity(context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        androidx.core.app.RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Write your reply")
                .build();

        Intent replyIntent = new Intent(context, NotificationChatReplyReceiver.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                0,replyIntent,0);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.send_button_bg,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Me");


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getString(R.string.chats_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(contentIntent)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setContentTitle(title)
                .setContentText(body);

        Notification n = mBuilder.build();

        n.defaults |= Notification.DEFAULT_ALL;
        mNotifyManager.notify(1, n);

    }

}
