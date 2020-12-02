package com.techknightsrtu.crosstalks.app.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techknightsrtu.crosstalks.app.feature.chat.ChatActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;

import java.util.List;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseCloudMessagingS";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


            Log.d(TAG, "onMessageReceived: FCM Message received" + remoteMessage.getData());

            String title = remoteMessage.getData().get("sender");
            String body = remoteMessage.getData().get("message");

            String avatarId = remoteMessage.getData().get("avatarId");
            String userId = remoteMessage.getData().get("userId");

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    getResources(),
                    Avatar.avatarList.get(Integer.parseInt(avatarId)));

            Context mActivity = getApplicationContext();

            if(!ChatActivity.isVisible)
                NotificationHelper.handleChatNotification(mActivity,title,body,avatarId,userId,largeIcon);

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

                if(tokens.isEmpty()){
                    tokens.add(newToken);
                    FirebaseMethods.setRegistrationToken(tokens);

                }else if(!tokens.contains(newToken)){
                    tokens.add(newToken);

                    FirebaseMethods.setRegistrationToken(tokens);
                }

            }
        });

    }
}
