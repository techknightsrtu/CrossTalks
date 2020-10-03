package com.techknightsrtu.crosstalks.helper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techknightsrtu.crosstalks.helper.interfaces.CollegeListCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseMethods {

    public static String getUserId(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public static boolean isUserSignedIn(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        return userId != null;
    }


    public static void setUserOnlineStatus(boolean status, String collegeName){

        if(isUserSignedIn()){

            Map<String,Boolean> userStatus = new HashMap<>();
            userStatus.put("status",status);

            FirebaseFirestore db  = FirebaseFirestore.getInstance();

            db.collection("onlineUsers")
                    .document(collegeName)
                    .collection("users")
                    .document(getUserId()).set(userStatus);

        }

    }

    public static void getCollegesFromDatabase(final CollegeListCallback callback){

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
                });

    }

}


