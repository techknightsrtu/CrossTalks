package com.techknightsrtu.crosstalks.app.feature.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetFeedbackFormUrl;

import java.util.Objects;

public class WebLinkOpenActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar pbSearchResult;
    private TextView tvActivityName;

    private String url = "https://cross-talks-e6d43.firebaseapp.com/#/";
    private String activityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();
        setupToolbar();

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                pbSearchResult.setProgress(progress); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    pbSearchResult.setVisibility(View.GONE);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    private void init() {
        pbSearchResult = findViewById(R.id.pbSearchResult);
        webView = findViewById(R.id.webView);
        tvActivityName = findViewById(R.id.tvActivityName);

        url = Objects.requireNonNull(getIntent().getExtras()).getString("url");
        activityName = getIntent().getExtras().getString("activity_name");

        tvActivityName.setText(activityName);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(WebLinkOpenActivity.this,R.color.bg_fill));
    }

    @Override
    public void onPause() {
        FirebaseMethods.setUserOnlineStatus("Offline");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseMethods.setUserOnlineStatus("Online");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}