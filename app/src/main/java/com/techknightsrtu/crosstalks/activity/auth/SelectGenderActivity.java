package com.techknightsrtu.crosstalks.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;

public class SelectGenderActivity extends AppCompatActivity {

    private static final String TAG = "SelectGenderActivity";

    // Widgets
    private TextView tvGreetings, tvGenderInfo;
    private ImageView ivMale, ivFemale;
    private Button btRegister;

    //LocalData
    UserProfileDataPref prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);

        init();

        setupGreetingsAndGenderInfo();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender = "";

                if( ivMale.getTag().equals("unclicked") && ivFemale.getTag().equals("unclicked")){

                    Toast.makeText(SelectGenderActivity.this, "Please choose your gender.", Toast.LENGTH_SHORT).show();

                }else{

                    if(ivMale.getTag().equals("clicked")){
                        gender = "male";
                    }else if(ivFemale.getTag().equals("clicked")){
                        gender = "female";
                    }
                    updateGenderInDatabase(gender);

                }

            }
        });

        ivMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ivMale.getTag().equals("unclicked")){
                    ivMale.setTag("clicked");
                    ivMale.setBackground(getDrawable(R.drawable.gender_clicked));
                    ivMale.setImageDrawable(getDrawable(R.drawable.ic_male_white));

                    ivFemale.setTag("unclicked");
                    ivFemale.setBackground(getDrawable(R.drawable.gender_unclicked));
                    ivFemale.setImageDrawable(getDrawable(R.drawable.ic_female_red));
                }
            }
        });

        ivFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ivFemale.getTag().equals("unclicked")){
                    ivFemale.setTag("clicked");
                    ivFemale.setBackground(getDrawable(R.drawable.gender_clicked));
                    ivFemale.setImageDrawable(getDrawable(R.drawable.ic_female_white));

                    ivMale.setTag("unclicked");
                    ivMale.setBackground(getDrawable(R.drawable.gender_unclicked));
                    ivMale.setImageDrawable(getDrawable(R.drawable.ic_male_red));
                }
            }
        });
    }

    private void updateGenderInDatabase(String gender) {
        Log.i(TAG, "updateGenderInDatabase: " + gender);

        prefs.setGender(gender);

        startActivity(new Intent(SelectGenderActivity.this,ChooseCollegeActivity.class));

    }

    private void setupGreetingsAndGenderInfo() {
        tvGreetings.setText(Html.fromHtml("<font color=red>Hi </font><font color=white>there 🙋‍♂️</font><font color=red> !‍"));
        tvGenderInfo.setText(Html.fromHtml("Select your <font color=red>Gender‍"));
    }

    private void init() {
        // Text Views
        tvGreetings = findViewById(R.id.tvGreetings);
        tvGenderInfo = findViewById(R.id.tvGenderInfo);

        // Image view
        ivMale = findViewById(R.id.ivMale);
        ivFemale = findViewById(R.id.ivFemale);

        // Button
        btRegister = findViewById(R.id.btRegister);

        prefs = new UserProfileDataPref(SelectGenderActivity.this);
    }


}