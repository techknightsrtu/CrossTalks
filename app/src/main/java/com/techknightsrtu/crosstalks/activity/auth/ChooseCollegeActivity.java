package com.techknightsrtu.crosstalks.activity.auth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.helper.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces.GetCollegeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ChooseCollegeActivity extends AppCompatActivity {

    private static final String TAG = "ChooseCollegeActivity";

    private Context mContext = ChooseCollegeActivity.this;

    private TextView tvChooseCollege;
    private Spinner spCollege;
    private UserProfileDataPref prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_college);

        init();
        setupTvChooseCollege();
        setupSpinner();

    }

    private void setupSpinner() {

        FirebaseMethods.getCollegesFromDatabase(new GetCollegeList() {
            @Override
            public void onCallback(Map<String, String> collegesList) {

                ArrayList<String> colleges = new ArrayList<>(collegesList.values());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item, colleges);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spCollege.setAdapter(adapter);

                //Save College Id's data to local cache
                prefs.setCollegeIdSet(collegesList.keySet());

            }
        });

    }

    private void setupTvChooseCollege() {
        tvChooseCollege.setText(Html.fromHtml("Choose your <font color=red>College 🏢"));
    }

    private void init() {
        tvChooseCollege = findViewById(R.id.tvChooseCollege);
        spCollege = findViewById(R.id.spCollege);
        prefs = new UserProfileDataPref(ChooseCollegeActivity.this);
    }

    public void noCollege(View view){
        //Handle no College user

        // Save user data to local cache
        prefs.setCollegeId("nc");
        prefs.setCollegeName("noCollege");

        startActivity(new Intent(ChooseCollegeActivity.this,ChooseAvatarActivity.class));

    }

    public void selectCollege(View view){


        //Handle user with college
        String collegeName = spCollege.getSelectedItem().toString();
        int position = spCollege.getSelectedItemPosition();

        Log.d(TAG, "selectCollege: " + collegeName);

        //Extract data from local cache to find out collegeId
        Set<String> collegeIdSet = prefs.getCollegeIdSet();
        ArrayList<String> idList = new ArrayList<>(collegeIdSet);

        String collegeId = idList.get(position);

        //Save user data to local cache
        prefs.setCollegeId(collegeId);
        prefs.setCollegeName(collegeName);

        startActivity(new Intent(ChooseCollegeActivity.this,ChooseAvatarActivity.class));

    }



}