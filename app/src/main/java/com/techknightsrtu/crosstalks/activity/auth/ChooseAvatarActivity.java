package com.techknightsrtu.crosstalks.activity.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.helper.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.Random;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAvatarActivity";

    // Widgets
    private TextView tvChooseAvatar;
    private TextView tvAvatarName;
    private ImageView ivAvatarImage;

    private ArrayList<Integer> avatarList;
    private ArrayList<String> nameList;

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

        avatarList = new ArrayList<>();
        avatarList.add(R.drawable.ic_mystique);
        avatarList.add(R.drawable.ic_rogue);
        avatarList.add(R.drawable.ic_wolverine);
        avatarList.add(R.drawable.ic_luke_cage);
        avatarList.add(R.drawable.ic_professor_x);
        avatarList.add(R.drawable.ic_captain_america);
        avatarList.add(R.drawable.ic_cyclops_marvel);
        avatarList.add(R.drawable.ic_frankensteins);
        avatarList.add(R.drawable.ic_gambit);
        avatarList.add(R.drawable.ic_hellcat);
        avatarList.add(R.drawable.ic_human_torch);

        nameList = new ArrayList<>();
        nameList.add("Hellcat");
        nameList.add("Wolverine");
        nameList.add("Rogue");
        nameList.add("Captain America");
        nameList.add("Human Torch");
        nameList.add("Gambit");
        nameList.add("Frankensteins");
        nameList.add("Cyclops Marvel");
        nameList.add("Professor X");
        nameList.add("Luke Cage");

    }

    public void newProfile(View view){
        generateProfile();
    }

    public void generateProfile(){

        // create instance of Random class
        Random rand = new Random();

        int randAvatarId = rand.nextInt(avatarList.size()-1);
        int randNameId = rand.nextInt(nameList.size()-1);

        ivAvatarImage.setImageResource(avatarList.get(randAvatarId));
        tvAvatarName.setText(nameList.get(randNameId));

    }

    public void createChatProfile(View view){



    }


}