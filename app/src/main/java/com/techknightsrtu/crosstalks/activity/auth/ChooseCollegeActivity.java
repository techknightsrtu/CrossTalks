package com.techknightsrtu.crosstalks.activity.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.NoAppAccessActivity;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.models.User;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChooseCollegeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppLocalData";
    private static final String TAG = "ChooseCollegeActivity";

    private TextView tvChooseCollege;
    private Spinner spCollege;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_college);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        init();
        setupTvChooseCollege();
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, getResources()
                .getStringArray(R.array.college_name));//setting the country_array to spinner

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCollege.setAdapter(adapter);
    }

    private void setupTvChooseCollege() {
        tvChooseCollege.setText(Html.fromHtml("Choose your <font color=red>College 🏢"));
    }

    private void init() {
        tvChooseCollege = findViewById(R.id.tvChooseCollege);

        spCollege = findViewById(R.id.spCollege);
    }

    public void noCollege(View view){
        //Handle no College user
        createNewUserInDatabase();
    }

    public void selectCollege(View view){

        //Handle user with college
        String collegeName = spCollege.getSelectedItem().toString();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("collegeName", collegeName);
        editor.apply();

        createNewUserInDatabase();

    }

    private void createNewUserInDatabase(){

        //Extract data from local cache
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = mAuth.getUid();
        String originalName = prefs.getString("originalName", "No name defined");
        String email = prefs.getString("email","");
        String gender = prefs.getString("gender","");
        String photoUrl = prefs.getString("photoUrl","");
        String collegeName = prefs.getString("collegeName","");

        String joiningDate = Utility.getCurrentTimestamp();

        // Create a new user with a first and last name
        User newUser = new User(userId,originalName,email,photoUrl,gender,collegeName,joiningDate);

        // Add a new document with a generated ID
        db.collection("users")
                .document(userId)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        if(Utility.isAppAccessAllowed()){

                            Intent i = new Intent(ChooseCollegeActivity.this,ChooseAvatarActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }else{

                            Intent i = new Intent(ChooseCollegeActivity.this, NoAppAccessActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(ChooseCollegeActivity.this, "User not created in database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}