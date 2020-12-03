package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;
import com.techknightsrtu.crosstalks.app.feature.home.adapter.UserChatAdapter;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.app.feature.profile.adapter.BlockedUserAdapter;
import com.techknightsrtu.crosstalks.app.feature.profile.interfaces.OnUnblockButtonClick;
import com.techknightsrtu.crosstalks.app.helper.Utility;
import com.techknightsrtu.crosstalks.app.models.BlockedUser;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.DoesUserExist;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCollegeList;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCurrentFCMToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetFeedbackFormUrl;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserOnlineStatus;

import com.techknightsrtu.crosstalks.app.models.User;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.IsUserBlocked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    public static String getUserId(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public static void isUserBlocked(String userId, String charUserId, IsUserBlocked isUserBlocked){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("blockedUser")
                .child(userId)
                .child(charUserId);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                isUserBlocked.onCallback(snapshot.exists());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static void checkIfUserExist(String userId, final DoesUserExist doesUserExist){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            if(task.getResult().isEmpty()){
                                doesUserExist.onCallback(false);
                            }else{
                                doesUserExist.onCallback(true);
                            }

                        }else{
                            Log.d(TAG, "onComplete: Something went wrong" + task.getResult());
                        }
                    }
                });

    }

    public static boolean isUserSignedIn(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        return userId != null;
    }

    public static void signOut(){

        //Delete Token from Database
        getRegistrationTokens(tokens -> getCurrentToken(currToken -> {

            tokens.remove(currToken);

            FirebaseFirestore db  = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(getUserId())
                    .update("tokens",tokens)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "signOut: " + tokens);

                        //Signing Out
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();

                    });

        }));

    }

    public static void setUserOnlineStatus(String status){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("onlineStatus")
                .child(getUserId());

        Map<String,Object> map = new HashMap<>();
        map.put("status",status);

        db.setValue(map);

    }

    public static void blockThisUser(String userId){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("blockedUser")
                .child(getUserId())
                .child(userId);

        Map<String,Object> map = new HashMap<>();
        map.put("timestamp", Utility.getCurrentTimestamp());

        db.setValue(map);

    }

    public static void unblockThisUser(String userId){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("blockedUser")
                .child(getUserId())
                .child(userId);

        db.removeValue();

    }



    public static void getUserOnlineStatus(String userId, final GetUserOnlineStatus getUserOnlineStatus){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("onlineStatus")
                .child(userId);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("status")){
                    String status = snapshot.child("status").getValue().toString();
                    Log.d(TAG, "onDataChange:  ONLINE STATUS" +  status);
                    getUserOnlineStatus.onCallback(status);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public static void getCollegesFromDatabase(final GetCollegeList callback){

        final Map<String,String> colleges = new HashMap<>();

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("colleges")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){

                        String key = ds.getId();
                        String collegeName = (String) ds.get("collegeName");

                        colleges.put(key,collegeName);

                    }

                    callback.onCallback(colleges);

                }).addOnFailureListener(e -> Log.d(TAG, "onComplete: Something went wrong" + e.getMessage()));

    }

    public static void getUserData(String userId, final GetUserData userDataCallback){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userDocumentSnapshot) {

                        if(Objects.equals(userDocumentSnapshot.get("collegeId").toString(), "nc")){

                            userDataCallback.onCallback(userDocumentSnapshot.toObject(User.class),
                                    "Chat Anonymously.");

                        }else{
                            //Fetch college Data
                            FirebaseFirestore db  = FirebaseFirestore.getInstance();
                            db.collection("colleges")
                                    .document(userDocumentSnapshot.get("collegeId").toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot collegeDocumentSnapshot) {

                                            Log.d(TAG, "onSuccess: " + collegeDocumentSnapshot.getData());

                                            String collegeName = collegeDocumentSnapshot.get("collegeName").toString();

                                            userDataCallback.onCallback(userDocumentSnapshot.toObject(User.class),
                                                    collegeName);
                                        }
                                    });
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onComplete: Something went wrong" + e.getMessage());
            }
        });

    }

    public static void getOnlyUserData(String userId, final GetOnlyUserData userDataCallback){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot userDocumentSnapshot) {

                        userDataCallback.onCallback(userDocumentSnapshot.toObject(User.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onComplete: Something went wrong" + e.getMessage());
            }
        });

    }

    public static void createNewUserInDatabase(String userId, User user, final CreateNewUser createNewUser){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        createNewUser.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        createNewUser.onCallback(false);
                    }
                });
    }


    public static BlockedUserAdapter setupUserBlockListAdapter(OnUnblockButtonClick onUnblockButtonClick, LinearLayout linearLayout){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("blockedUser")
                .child(getUserId());

        db.keepSynced(true);

        FirebaseRecyclerOptions<BlockedUser> options = new FirebaseRecyclerOptions.Builder<BlockedUser>()
                .setQuery(db,BlockedUser.class)
                .build();

        return new BlockedUserAdapter(options,onUnblockButtonClick,linearLayout);

    }



    public static UserChatAdapter setupOnlineChatsAdapter(String collegeId, OnChatButtonClick onChatButtonClick){

        final FirebaseFirestore db  = FirebaseFirestore.getInstance();

        CollectionReference collRef = db.collection("users");

        Query q = collRef.whereEqualTo("collegeId",collegeId);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(q,User.class)
                .build();

        return new UserChatAdapter(options,onChatButtonClick);

    }



    // REGION : Firebase Cloud Messaging

    public static void getCurrentToken(final GetCurrentFCMToken getCurrentFCMToken){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                getCurrentFCMToken.onCallback(s);
            }
        });
    }

    public static void setRegistrationToken(List<String> tokens){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(getUserId())
                .update("tokens",tokens);

    }

    public static void getRegistrationTokens(final GetRegistrationToken getRegistrationToken){

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        if(isUserSignedIn()){
            db.collection("users")
                    .document(getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot ds) {

                            User user = ds.toObject(User.class);

                            getRegistrationToken.onCallback(user.getTokens());

                        }
                    });
        }

    }

    public static void getUrlFromDatabase(String urlName, final GetFeedbackFormUrl getFeedbackFormUrl){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("urls");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String url = snapshot.child(urlName).getValue().toString();
                    Log.d(TAG, "onDataChange:  FEEDBACK FORM URL" +  url);
                    getFeedbackFormUrl.onCallback(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}


