package com.techknightsrtu.crosstalks.helper.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.DoesUserExist;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.GetCollegeList;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.GetUserData;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.OnlineUsersFromCollege;
import com.techknightsrtu.crosstalks.models.User;

import java.util.ArrayList;
import java.util.HashMap;
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

        db.collection("onlineUsers")
                .document(collegeId)
                .collection("users")
                .whereEqualTo("status",true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot ds: queryDocumentSnapshots.getDocuments()) {

                            String userId = ds.getId();

                            if(!userId.equals(getUserId())){

                                Log.d(TAG, "onSuccess: user Id" + userId);
                                onlineUserList.add(userId);

                            }

                        }

                        onlineUsersFromCollege.onCallback(onlineUserList);
                    }
                });

    }

}


