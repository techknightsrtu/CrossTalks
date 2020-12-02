package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.techknightsrtu.crosstalks.app.feature.home.adapter.UserChatAdapter;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.DoesUserExist;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCollegeList;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCurrentFCMToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserOnlineStatus;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.OnlineUsersFromCollege;
import com.techknightsrtu.crosstalks.app.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    public static String getUserId(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
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

            setRegistrationToken(tokens);

            Log.d(TAG, "signOut: " + tokens);

            //Signing Out
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();

        }));

    }

    public static void setUserOnlineStatus(String status){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("onlineStatus")
                .child(getUserId());

        Map<String,Object> map = new HashMap<>();
        map.put("status",status);

        db.setValue(map);

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


    public static void getOnlineUserFromCollege(String collegeId,
                                                final OnlineUsersFromCollege onlineUsersFromCollege){

        final ArrayList<String> onlineUserList = new ArrayList<>();

        final FirebaseFirestore db  = FirebaseFirestore.getInstance();

        CollectionReference collRef = db.collection("users");

        collRef.whereEqualTo("collegeId",collegeId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        onlineUserList.clear();

                        for (DocumentSnapshot ds: value.getDocuments()) {

                            if (ds != null && ds.exists()) {

                                Log.d(TAG, "Current data: " + ds.getData());

                                String userId = ds.getId();

                                if(!userId.equals(getUserId())){

                                    Log.d(TAG, "onSuccess: user Id" + userId);
                                    onlineUserList.add(userId);

                                }

                            } else {
                                Log.d(TAG, "Current data: null");
                            }

                        }

                        onlineUsersFromCollege.onCallback(onlineUserList);
                    }
                });

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

}


