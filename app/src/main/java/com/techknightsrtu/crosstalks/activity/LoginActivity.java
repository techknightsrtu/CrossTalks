package com.techknightsrtu.crosstalks.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Widgets
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        setupAppName();

    }

    private void setupAppName() {
        tvAppName.setText(Html.fromHtml("Cross <font color=red> Talks"));
    }

    private void init() {
        // Text view
        tvAppName = findViewById(R.id.tvAppName);
    }

    public void SignInWithGoogle(View view) {
    }
}