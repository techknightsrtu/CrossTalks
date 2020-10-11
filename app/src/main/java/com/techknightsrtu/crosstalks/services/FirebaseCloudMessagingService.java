package com.techknightsrtu.crosstalks.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;

import java.util.List;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseCloudMessagingS";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null){

            //todo: Show notification
            Log.d(TAG, "onMessageReceived: FCM Message received" + remoteMessage.getData());

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
