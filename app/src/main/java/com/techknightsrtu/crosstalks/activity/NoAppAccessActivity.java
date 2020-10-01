package com.techknightsrtu.crosstalks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.techknightsrtu.crosstalks.R;

public class NoAppAccessActivity extends AppCompatActivity {

    private static final String TAG = "NoAppAccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_app_access);
    }

}