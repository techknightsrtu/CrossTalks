package com.techknightsrtu.crosstalks.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.NoAppAccessActivity;
import com.techknightsrtu.crosstalks.activity.chat.HomeActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.models.User;

import java.util.Random;

import static com.techknightsrtu.crosstalks.helper.Avatar.avatarList;
import static com.techknightsrtu.crosstalks.helper.Avatar.nameList;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAvatarActivity";

    // Widgets
    private TextView tvChooseAvatar;
    private TextView tvAvatarName;
    private ImageView ivAvatarImage, ivGenerateNewAvatar;

    //LocalData
    UserProfileDataPref prefs;

    //Variables
    private int avatarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);

        init();

        setupTvChooseAvatar();

        generateProfile();

    }

    private void setupTvChooseAvatar() {
        tvChooseAvatar.setText(Html.fromHtml("Choose your <font color=red>Avatar 😎"));
    }

    private void init() {
        // Text view
        tvChooseAvatar = findViewById(R.id.tvChooseAvatar);
        tvAvatarName = findViewById(R.id.tvAvatarName);

        // ImageView
        ivAvatarImage = findViewById(R.id.ivUserAvatar);
        ivGenerateNewAvatar = findViewById(R.id.ivGenerateNewAvatar);

        prefs = new UserProfileDataPref(ChooseAvatarActivity.this);

    }

    public void newProfile(View view){
        generateProfile();
    }

    public void generateProfile(){

        // For rotating generate icon
        ivGenerateNewAvatar.startAnimation(AnimationUtils.loadAnimation(ChooseAvatarActivity.this, R.anim.rotate));

        // create instance of Random class
        Random rand = new Random();
        avatarId = rand.nextInt(avatarList.size()-1);

        ivAvatarImage.setImageResource(avatarList.get(avatarId));
        tvAvatarName.setText(nameList.get(avatarId));

    }

    public void createChatProfile(View view){

        prefs.setAvatarId(String.valueOf(avatarId));
        prefs.setJoiningDate(Utility.getCurrentTimestamp());

        createNewUserInDatabase();

    }

    private void createNewUserInDatabase(){

        //Extract data from local cache

        String userId = prefs.getUserId();
        Log.d(TAG, "createNewUserInDatabase: userId" + userId);
        String originalName = prefs.getOriginalName();
        String email = prefs.getEmail();
        String gender = prefs.getGender();
        String photoUrl = prefs.getPhotoUrl();
        String collegeId = prefs.getCollegeId();
        String joiningDate = prefs.getJoiningDate();

        // Create a new user with a first and last name
        User newUser = new User(userId,String.valueOf(avatarId),
                originalName,email,photoUrl,gender,collegeId,joiningDate);

        FirebaseMethods.createNewUserInDatabase(userId, newUser, new CreateNewUser() {
            @Override
            public void onCallback(boolean done) {

                if(done){

                    if(Utility.isAppAccessAllowed()){

                        Intent i = new Intent(ChooseAvatarActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }else{

                        Intent i = new Intent(ChooseAvatarActivity.this, NoAppAccessActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }

                }else{
                    Log.w(TAG, "Error adding document");
                    Toast.makeText(ChooseAvatarActivity.this, "User not created in database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}