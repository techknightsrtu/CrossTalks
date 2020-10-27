package com.techknightsrtu.crosstalks.firebase;

import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techknightsrtu.crosstalks.activity.chat.models.ChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetLastMessage;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetMessagesFromChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRecentChats;
import com.techknightsrtu.crosstalks.helper.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMethods {

    private static final String TAG = "ChatMethods";

    public static void setChannelLastActiveStatus(String timestamp, String sender, String receiver){

        DatabaseReference userChatChannels = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels");

        Map<String,Object> mp = new HashMap<>();
        mp.put("containsChats","true");
        mp.put("lastActive",timestamp);

        userChatChannels.child(sender)
                .child(receiver)
                .updateChildren(mp);

        userChatChannels.child(receiver)
                .child(sender)
                .updateChildren(mp);

    }

    public static void getOrCreateChatChannel(final String sender, final String receiver, final GetChatChannel getChatChannel){

        final DatabaseReference currentUserChatChannels = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels")
                .child(sender);

        currentUserChatChannels.child(receiver)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("channelId")){
                            getChatChannel.onCallback(snapshot.child("channelId").getValue().toString());
                        }else{

                            DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                                    .child("chatChannels").push();

                            ArrayList<String> list = new ArrayList<>();
                            list.add(sender);
                            list.add(receiver);

                            ChatChannel ch = new ChatChannel(list);
                            ch.setChannelId(chatChannelsRef.getKey());

                            Log.d(TAG, "onDataChange: CHANNEL CREATED " + chatChannelsRef.getKey());

                            chatChannelsRef.setValue(ch);

                            Map<String,String> mp = new HashMap<>();
                            mp.put("channelId",ch.getChannelId());
                            mp.put("containsChats","false");
                            mp.put("lastActive",Utility.getCurrentTimestamp());


                            currentUserChatChannels
                                    .child(receiver)
                                    .setValue(mp);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("engagedChatChannels")
                                    .child(receiver)
                                    .child(sender)
                                    .setValue(mp);

                            Log.d(TAG, "onDataChange: " + ch.getChannelId());

                            getChatChannel.onCallback(ch.getChannelId());

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void sendTextMessage(String channelId, Message message){

        Log.d(TAG, "sendTextMessage: " + message.toString());
        Log.d(TAG, "sendTextMessage: THIS IS CHANNEL " + channelId);

        DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId).child("messages");

        chatChannelsRef.push().setValue(message);

    }

    public static void getMessages(String channelId, final GetMessagesFromChannel getMessagesFromChannel){


        Log.d(TAG, "getMessages: Message Call done");

        DatabaseReference chatChannelRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId).child("messages");

        chatChannelRef.keepSynced(true);

        Log.d(TAG, "getMessages: " + channelId);

        chatChannelRef
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot value) {

                        Log.d(TAG, "onDataChange: Messages Received" + value);

                        ArrayList<Message> list = new ArrayList<>();

                        for (DataSnapshot ds: value.getChildren()) {

                            if (ds != null && ds.exists()) {

                                Log.d(TAG, "onDataChange: " + ds);
                                list.add(ds.getValue(Message.class));

                            } else {
                                Log.d(TAG, "Current data: null");
                            }

                        }
                        getMessagesFromChannel.onCallback(list);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Listen failed.");
                    }
                });

    }

    public static void getLastMessage(String channelId, final GetLastMessage getLastMessage){

        DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId);

        chatChannelsRef.keepSynced(true);

        chatChannelsRef.child("messages")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot value) {

                        for (DataSnapshot ds: value.getChildren()) {
                            getLastMessage.onCallback(ds.getValue(Message.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void getRecentChats(String userId, final GetRecentChats getRecentChats){

        final ArrayList<Map<String,String>> recentChatsList = new ArrayList<>();

        DatabaseReference collRef = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels").child(userId);

        collRef.keepSynced(true);

        collRef.orderByChild("lastActive")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot value) {

                        recentChatsList.clear();

                        for(DataSnapshot ds : value.getChildren()){

                            if(ds.child("containsChats").getValue().toString().equals("true")){

                                Map<String,String> chatList = new HashMap<>();
                                chatList.put("userId",ds.getKey());
                                chatList.put("channelId",ds.child("channelId").getValue().toString());

                                recentChatsList.add(chatList);

                            }

                        }

                        Collections.reverse(recentChatsList);

                        getRecentChats.onCallback(recentChatsList);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static ValueEventListener updateSeenMessage(String channelId,
                                                         final String sender,
                                                         final String receiver){

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels")
                .child(channelId)
                .child("messages");

        return db.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot value) {

                 for (DataSnapshot ds: value.getChildren()) {

                     Message m = ds.getValue(Message.class);
                     if(m.getReceiver().equals(sender) && m.getSender().equals(receiver)){
                         Map<String,Object> map = new HashMap<>();
                         map.put("isSeen",true);
                         ds.getRef().updateChildren(map);
                     }
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


    }

    public static void removeChatSeenListener(String channelId,ValueEventListener vl){

         DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels")
                .child(channelId)
                .child("messages");

         db.removeEventListener(vl);

    }

}
