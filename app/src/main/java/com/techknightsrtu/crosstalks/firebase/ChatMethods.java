package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.techknightsrtu.crosstalks.app.feature.home.adapter.RecentChatAdapter;
import com.techknightsrtu.crosstalks.app.feature.chat.adapter.MessagesAdapter;
import com.techknightsrtu.crosstalks.app.feature.chat.models.ChatChannel;
import com.techknightsrtu.crosstalks.app.feature.chat.models.EngagedChatChannel;
import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetChatChannel;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetLastMessage;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserTypingStatus;
import com.techknightsrtu.crosstalks.app.helper.Utility;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.IsChatDeleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatMethods {

    private static final String TAG = "ChatMethods";

    public static void checkIfChatUserDeletedChat(String channelId, String chatUser, IsChatDeleted isChatDeleted){

        DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId);

        chatChannelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                isChatDeleted.onCallback(Objects.equals(snapshot.child("userIds").getValue().toString(), chatUser));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void setChannelLastActiveStatus(String timestamp, String sender, String receiver){

        final DocumentReference currentUserDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(sender);

        Map<String,Object> mp = new HashMap<>();
        mp.put("containsChats","true");
        mp.put("lastActive",timestamp);

        currentUserDocRef
                .collection("engagedChatChannels")
                .document(receiver)
                .update(mp);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(receiver)
                .collection("engagedChatChannels")
                .document(sender)
                .update(mp);;

    }

    public static void getOrCreateChatChannel(final String sender, final String receiver, final GetChatChannel getChatChannel){

        final DocumentReference currentUserDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(sender);

        currentUserDocRef.collection("engagedChatChannels")
                .document(receiver)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        assert documentSnapshot != null;
                        if(documentSnapshot.contains("channelId")){

                            getChatChannel.onCallback(documentSnapshot.get("channelId").toString());

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

                            currentUserDocRef
                                    .collection("engagedChatChannels")
                                    .document(receiver)
                                    .set(mp);

                            FirebaseFirestore.getInstance()
                                    .collection("users").document(receiver)
                                    .collection("engagedChatChannels")
                                    .document(sender)
                                    .set(mp);

                            getChatChannel.onCallback(ch.getChannelId());

                        }
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


    public static void removeCurrentUserFromChatChannel(String channelId, String chatUser){

        DatabaseReference userChatChannel = FirebaseDatabase.getInstance().getReference()
                .child("engagedChatChannels").child(FirebaseMethods.getUserId()).child(chatUser);
        userChatChannel.removeValue();


        DatabaseReference chatChannelsRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId);

        chatChannelsRef.child(FirebaseMethods.getUserId()).removeValue();

        Map<String,Object> map = new HashMap<>();
        map.put("userIds",chatUser);

        chatChannelsRef.updateChildren(map);

    }



    //FIREBASE RECYCLER ADAPTER
    public static MessagesAdapter setupFirebaseChatsAdapter(String channelId, LinearLayout llSafetyLayout){

        DatabaseReference chatChannelRef = FirebaseDatabase.getInstance().getReference()
                .child("chatChannels").child(channelId).child("messages");


        Query q = chatChannelRef.limitToLast(50);

        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(q,Message.class)
                .build();

        return new MessagesAdapter(options,llSafetyLayout);

    }

    public static RecentChatAdapter setupFirebaseRecentChatsAdapter(String userId, OnChatButtonClick onChatButtonClick, LinearLayout llEmptyView){

        final FirebaseFirestore db  = FirebaseFirestore.getInstance();

        CollectionReference collRef = db.collection("users")
                .document(userId)
                .collection("engagedChatChannels");

        com.google.firebase.firestore.Query q =
                collRef.whereEqualTo("containsChats","true")
                .orderBy("lastActive");

        FirestoreRecyclerOptions<EngagedChatChannel> options =
                new FirestoreRecyclerOptions.Builder<EngagedChatChannel>()
                .setQuery(q,EngagedChatChannel.class)
                .build();

        return new RecentChatAdapter(options,onChatButtonClick, llEmptyView);

    }


}
