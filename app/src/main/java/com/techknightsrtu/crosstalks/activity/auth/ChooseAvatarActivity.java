package com.techknightsrtu.crosstalks.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import java.util.Random;

import static com.techknightsrtu.crosstalks.helper.Avatar.avatarList;
import static com.techknightsrtu.crosstalks.helper.Avatar.nameList;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAvatarActivity";

    // Widgets
    private TextView tvChooseAvatar;
    private TextView tvAvatarName;
    private ImageView ivAvatarImage;


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
        ivAvatarImage = findViewById(R.id.ivUserAvatar);

    }

    public void newProfile(View view){
        generateProfile();
    }

    public void generateProfile(){

        // create instance of Random class
        Random rand = new Random();
        int randNum = rand.nextInt(avatarList.size()-1);

        ivAvatarImage.setImageResource(avatarList.get(randNum));
        tvAvatarName.setText(nameList.get(randNum));

    }

    public void createChatProfile(View view){


    }


}