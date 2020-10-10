package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.techknightsrtu.crosstalks.activity.chat.models.ChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetMessagesFromChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRecentChats;
import com.techknightsrtu.crosstalks.helper.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMethods {

    private static final String TAG = "ChatMethods";


    public static void getOrCreateChatChannel(final String sender, final String receiver, final GetChatChannel getChatChannel){

         final DocumentReference currentUserDocRef = FirebaseFirestore.getInstance().collection("users").document(sender);

         currentUserDocRef.collection("engagedChatChannels")
                 .document(receiver)
                 .get()
                 .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {

                         if(documentSnapshot.contains("channelId")){
                             getChatChannel.onCallback(documentSnapshot.get("channelId").toString());
                         }else{
                             DocumentReference chatChannelsRef = FirebaseFirestore.getInstance()
                                     .collection("chatChannels").document();

                             ArrayList<String> list = new ArrayList<>();
                             list.add(sender);
                             list.add(receiver);

                             ChatChannel ch = new ChatChannel(list);
                             ch.setChannelId(chatChannelsRef.getId());

                             chatChannelsRef.set(ch);

                             Map<String,String> mp = new HashMap<>();
                             mp.put("channelId",ch.getChannelId());

                             currentUserDocRef
                                     .collection("engagedChatChannels")
                                     .document(receiver)
                                     .set(mp);

                             FirebaseFirestore.getInstance().collection("users").document(receiver)
                                     .collection("engagedChatChannels")
                                     .document(sender)
                                     .set(mp);

                             getChatChannel.onCallback(ch.getChannelId());
                         }

                     }
                 });


    }


    public static void sendTextMessage(String channelId, Message message){

        DocumentReference chatChannelsRef = FirebaseFirestore.getInstance()
                .collection("chatChannels").document(channelId);

        chatChannelsRef.collection("messages").add(message);

    }

    public static void getMessages(String channelId, final GetMessagesFromChannel getMessagesFromChannel){



        DocumentReference chatChannelsRef = FirebaseFirestore.getInstance()
                .collection("chatChannels").document(channelId);

        chatChannelsRef.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        ArrayList<Message> list = new ArrayList<>();

                        for (DocumentSnapshot ds: value.getDocuments()) {

                            if (ds != null && ds.exists()) {

                                list.add(ds.toObject(Message.class));

                            } else {
                                Log.d(TAG, "Current data: null");
                            }

                        }

                        getMessagesFromChannel.onCallback(list);

                    }
                });

    }
    

    public static void getRecentChats(String userId, GetRecentChats getRecentChats){

        final ArrayList<String> recentChatsList = new ArrayList<>();
        final FirebaseFirestore db  = FirebaseFirestore.getInstance();

        CollectionReference collRef = db.collection("users")
                .document(userId)
                .collection("engagedChatChannels");

        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                for(DocumentSnapshot ds : value.getDocuments()){

                    recentChatsList.add(ds.getId());

                }

            }
        });

    }
}
