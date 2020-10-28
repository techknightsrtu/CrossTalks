package com.techknightsrtu.crosstalks.firebase;

import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.techknightsrtu.crosstalks.activity.chat.adapter.ChatAdapter;
import com.techknightsrtu.crosstalks.activity.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.activity.chat.models.ChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.EngagedChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.MessageItemViewHolder;
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

    public static ChatAdapter setupFirebaseRecentChatsAdapter(String userId, OnChatButtonClick onChatButtonClick){

        DatabaseReference userChatChannels = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels").child(userId);

        userChatChannels.keepSynced(true);

        Query q = userChatChannels.orderByChild("lastActive");

        FirebaseRecyclerOptions<EngagedChatChannel> options =
                new FirebaseRecyclerOptions.Builder<EngagedChatChannel>()
                .setQuery(q,EngagedChatChannel.class)
                .build();

        return new ChatAdapter(options,onChatButtonClick);

    }
}
