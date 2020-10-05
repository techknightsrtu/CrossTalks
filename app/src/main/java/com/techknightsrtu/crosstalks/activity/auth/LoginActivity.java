package com.techknightsrtu.crosstalks.activity.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.NoAppAccessActivity;
import com.techknightsrtu.crosstalks.activity.chat.HomeActivity;
import com.techknightsrtu.crosstalks.helper.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.UserProfileDataPref;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.helper.interfaces.DoesUserExist;
import com.techknightsrtu.crosstalks.helper.interfaces.GetUserData;
import com.techknightsrtu.crosstalks.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1000;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    // Widgets
    private TextView tvAppName;
    private View parentLayout;

    //LocalData
    UserProfileDataPref prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setupAppName();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        setupGoogleSignInClient();

    }


    private void setupAppName() {
        tvAppName.setText(Html.fromHtml("Cross <font color=red> Talks"));
    }

    private void init() {
        prefs = new UserProfileDataPref(LoginActivity.this);
        // Text view
        tvAppName = findViewById(R.id.tvAppName);
        parentLayout = findViewById(android.R.id.content);



    }

    private void setupGoogleSignInClient(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void signInWithGoogle(View view) {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

                prefs.setOriginalName(account.getDisplayName());
                prefs.setEmail(account.getEmail());
                prefs.setPhotoUrl(String.valueOf(account.getPhotoUrl()));

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredentialForFirebase:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            Log.d("User : ", userId);

                            ifUserExist(userId);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredentialForFirebase:failure", task.getException());
                            Snackbar.make(parentLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void ifUserExist(final String userId){

        FirebaseMethods.checkIfUserExist(userId, new DoesUserExist() {
            @Override
            public void onCallback(boolean exist) {
                if(!exist){
                    //send user to gender activity

                    startActivity(new Intent(LoginActivity.this,SelectGenderActivity.class));

                }else{

                    Log.d(TAG, "onCallback: LOGIN SUCCESS");
                    saveUserDataLocally(userId);

                    //send user to home activity
                    if(Utility.isAppAccessAllowed()){ ;

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }else{

                        Intent i = new Intent(LoginActivity.this,NoAppAccessActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }
                }
            }
        });

    }


    private void saveUserDataLocally(String userId){

        FirebaseMethods.getUserData(userId, new GetUserData() {
            @Override
            public void onCallback(User user,String collegeName) {

                Log.d(TAG, "onCallback: userData " + user.toString() + collegeName);

                prefs.setUserId(user.getUserId());
                prefs.setAvatarId(user.getAvatarId());
                prefs.setOriginalName(user.getOriginalName());
                prefs.setEmail(user.getEmail());
                prefs.setPhotoUrl(user.getPhotoUrl());
                prefs.setGender(user.getGender());
                prefs.setCollegeId(user.getCollegeId());
                prefs.setJoiningDate(user.getJoiningDate());
                prefs.setCollegeName(collegeName);

            }
        });

    }

}