package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techknightsrtu.crosstalks.activity.chat.adapter.ChatAdapter;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.models.ChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.EngagedChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetLastMessage;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserTypingStatus;
import com.techknightsrtu.crosstalks.helper.Utility;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static void deleteChatChannelIfNoChat(String sender, String receiver){

        final DatabaseReference currentUserChatChannels = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels")
                .child(sender);

        currentUserChatChannels.child(receiver)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("channelId")){

                            String channelId = snapshot.child("channelId").getValue().toString();
                            String containsChat = snapshot.child("containsChats").getValue().toString();

                            if(!Boolean.parseBoolean(containsChat)){

                                DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                                        .child("chatChannels").child(channelId);

                                chatChannelsRef.setValue("");

                                currentUserChatChannels
                                        .child(receiver)
                                        .setValue("");

                                FirebaseDatabase.getInstance().getReference()
                                        .child("engagedChatChannels")
                                        .child(receiver)
                                        .child(sender)
                                        .setValue("");

                            }

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


    public static void setUserTypingStatus(String channelId, boolean typingStatus){

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("chatChannels")
                .child(channelId)
                .child(FirebaseMethods.getUserId())
                .child("typingStatus")
                .setValue(typingStatus);

    }

    public static void getUserTypingStatus(String channelId, String chatUserId,
                                           GetUserTypingStatus getUserTypingStatus){

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("chatChannels")
                .child(channelId)
                .child(chatUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("typingStatus")){
                            boolean typingStatus = Boolean.parseBoolean(snapshot.child("typingStatus").getValue().toString());
                            getUserTypingStatus.onCallback(typingStatus);
                        }
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

    public static void getUnseenMessages(String channelId){

        DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId);

        chatChannelsRef.child("messages")
                .orderByChild("isSeen")
                .equalTo("false")
                .limitToLast(10);


    }



    //FIREBASE RECYCLER ADAPTER
    public static MessagesAdapter setupFirebaseChatsAdapter(String channelId){

        DatabaseReference chatChannelRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId).child("messages");

        chatChannelRef.keepSynced(true);

        Query q = chatChannelRef.limitToLast(50);

        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(q,Message.class)
                .build();

        return new MessagesAdapter(options);

    }

    public static ChatAdapter setupFirebaseRecentChatsAdapter(String userId, OnChatButtonClick onChatButtonClick, LinearLayout llEmptyView){

        DatabaseReference userChatChannels = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels").child(userId);

        userChatChannels.keepSynced(true);

        Query q = userChatChannels.orderByChild("lastActive");

        FirebaseRecyclerOptions<EngagedChatChannel> options =
                new FirebaseRecyclerOptions.Builder<EngagedChatChannel>()
                .setQuery(q,EngagedChatChannel.class)
                .build();

        return new ChatAdapter(options,onChatButtonClick, llEmptyView);

    }




}
