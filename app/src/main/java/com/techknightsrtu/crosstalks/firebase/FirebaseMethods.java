package com.techknightsrtu.crosstalks.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.DoesUserExist;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCollegeList;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCurrentFCMToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetRegistrationToken;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.OnlineUsersFromCollege;
import com.techknightsrtu.crosstalks.models.User;

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

    public static boolean isUserSignedIn(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        return userId != null;
    }

    public static void signOut(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
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

    public static void setUserOnlineStatus(boolean status, String collegeId){

        if(isUserSignedIn()){

            Map<String,Boolean> userStatus = new HashMap<>();
            userStatus.put("status",status);

            FirebaseFirestore db  = FirebaseFirestore.getInstance();

            db.collection("onlineUsers")
                    .document(collegeId)
                    .collection("users")
                    .document(getUserId()).set(userStatus);

        }

    }

    public static void getCollegesFromDatabase(final GetCollegeList callback){

        final Map<String,String> colleges = new HashMap<>();

        FirebaseFirestore db  = FirebaseFirestore.getInstance();

        db.collection("colleges")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){

                            String key = ds.getId();
                            String collegeName = (String) ds.get("collegeName");

                            colleges.put(key,collegeName);

                        }

                        callback.onCallback(colleges);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onComplete: Something went wrong" + e.getMessage());
            }
        });

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


