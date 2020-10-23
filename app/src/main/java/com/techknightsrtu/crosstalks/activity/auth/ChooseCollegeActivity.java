package com.techknightsrtu.crosstalks.activity.auth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCollegeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ChooseCollegeActivity extends AppCompatActivity {

    private static final String TAG = "ChooseCollegeActivity";

    private Context mContext = ChooseCollegeActivity.this;

    private TextView tvChooseCollege;
    private AutoCompleteTextView atvCollegeName;
    private UserProfileDataPref prefs;

    private ArrayList<String> colleges;

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

                colleges = new ArrayList<>(collegesList.values());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item, colleges);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                atvCollegeName.setAdapter(adapter);

                //Save College Id's data to local cache
                prefs.setCollegeIdSet(collegesList.keySet());

            }
        });

    }

    private void setupTvChooseCollege() {
        tvChooseCollege.setText(Html.fromHtml("Choose your <font color=red>College 🏢"));
    }

    private void init() {
        atvCollegeName = findViewById(R.id.atvCollegeName);
        tvChooseCollege = findViewById(R.id.tvChooseCollege);
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
        String collegeName = atvCollegeName.getText().toString().trim();

        if(!collegeName.isEmpty()){
            int position = colleges.indexOf(collegeName);

            Log.d(TAG, "selectCollege: " + collegeName + " : " + position);

            //Extract data from local cache to find out collegeId
            Set<String> collegeIdSet = prefs.getCollegeIdSet();
            ArrayList<String> idList = new ArrayList<>(collegeIdSet);

            String collegeId = idList.get(position);

            //Save user data to local cache
            prefs.setCollegeId(collegeId);
            prefs.setCollegeName(collegeName);

            startActivity(new Intent(ChooseCollegeActivity.this,ChooseAvatarActivity.class));
        }else {
            Toast.makeText(mContext, "Please choose or enter your college name.", Toast.LENGTH_SHORT).show();
        }

    }



}