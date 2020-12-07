package com.techknightsrtu.crosstalks.app.feature.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.techknightsrtu.crosstalks.BuildConfig;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.SplashActivity;
import com.techknightsrtu.crosstalks.app.feature.home.HomeActivity;
import com.techknightsrtu.crosstalks.app.helper.ProgressDialog;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetFeedbackFormUrl;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetVersionDetails;
import com.techknightsrtu.crosstalks.google_admob.GoogleAdMob;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private boolean isUpdateAvailable = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    // Widgets
    private ImageView ivBack, ivUserAvatar;
    private ImageView ivUpdateIndicator;
    private CircleImageView ivUserRealAvatar;
    private TextView tvLogOut, tvUserName,tvUserOriginalName, tvUserCollegeName, tvShareApp, tvRateUs, tvVersionName;

    // Google banner ad
    private FrameLayout ad_view_container;

    // User Data Pref
    private UserProfileDataPref profileDataPref;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAnalytics =  FirebaseAnalytics.getInstance(this);

        init();

        // For Loading Ads
        SharedPreferences pref = getApplicationContext().getSharedPreferences("adPref", 0); // 0 - for private mode
        if (pref.getBoolean("showAd", false)){
            GoogleAdMob googleAdMob = new GoogleAdMob(ProfileActivity.this, ad_view_container);
            googleAdMob.loadAd();
        }

        setupUserProfile();
    }

    private void setupUserProfile() {
        ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(profileDataPref.getAvatarId())));
        tvUserName.setText(Avatar.nameList.get(Integer.parseInt(profileDataPref.getAvatarId())));

        Glide.with(ProfileActivity.this)
                .load(profileDataPref.getPhotoUrl())
                .into(ivUserRealAvatar);

        String name = "(" + profileDataPref.getOriginalName() + ")";
        tvUserOriginalName.setText(name);
        tvUserCollegeName.setText(profileDataPref.getCollegeName());
    }

    private void init() {
        ivUpdateIndicator = findViewById(R.id.ivUpdateIndicator);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ivUserRealAvatar = findViewById(R.id.ivUserRealAvatar);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserOriginalName = findViewById(R.id.tvUserOriginalName);
        tvUserCollegeName = findViewById(R.id.tvUserCollegeName);
        tvVersionName = findViewById(R.id.tvVersionName);

        tvVersionName.setText("Version " + BuildConfig.VERSION_NAME);

        progressDialog = new ProgressDialog(ProfileActivity.this);

        FirebaseMethods.getAppVersionDetails(new GetVersionDetails() {
            @Override
            public void onCallback(int versionCode, String versionName) {
                int currentVersionCode = BuildConfig.VERSION_CODE;

                if(versionCode > currentVersionCode){
                    isUpdateAvailable = true;
                    ivUpdateIndicator.setVisibility(View.VISIBLE);
                }else{
                    isUpdateAvailable = false;
                    ivUpdateIndicator.setVisibility(View.GONE);
                }
            }
        });

        tvShareApp = findViewById(R.id.tvShareApp);
        tvShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossTalks");
                    String shareMessage= "\nBored in this Quarantine ??? Let's have some fun, Download CrossTalks and chat with your mates anonymously.   \n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                    Bundle bundle = new Bundle();
                    bundle.putString("Invite", "done");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

                    startActivity(Intent.createChooser(shareIntent, "Invite your friends"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        tvRateUs = findViewById(R.id.tvRateUs);
        tvRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        ad_view_container = findViewById(R.id.ad_view_container);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvLogOut = findViewById(R.id.tvLogOut);
        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMethods.signOut();
                Intent i = new Intent(ProfileActivity.this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        profileDataPref = new UserProfileDataPref(ProfileActivity.this);
    }

    public void CheckUpdate(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View updateDialogView = factory.inflate(R.layout.app_update_dialog, null);
        final AlertDialog updateDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.CustomDialogTheme)
        ).create();
        updateDialog.setView(updateDialogView);
        Window window = updateDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvAppUpdated = updateDialogView.findViewById(R.id.tvAppUpdated);
        TextView tvUpdateAvailable = updateDialogView.findViewById(R.id.tvUpdateAvailable);
        TextView tvVersionName = updateDialogView.findViewById(R.id.tvVersionName);

        tvVersionName.setText("Version " + BuildConfig.VERSION_NAME);

        updateDialogView.findViewById(R.id.tvUpdateAvailable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                final String appPackageName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                updateDialog.dismiss();
            }
        });

        updateDialog.show();

        if(isUpdateAvailable){
            tvUpdateAvailable.setVisibility(View.VISIBLE);
            tvAppUpdated.setVisibility(View.GONE);
        }else {
            tvUpdateAvailable.setVisibility(View.GONE);
            tvAppUpdated.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    public void BlockedUsers(View view) {
        startActivity(new Intent(ProfileActivity.this, BlockUserActivity.class));
    }

    public void Feedback(View view) {
        progressDialog.showProgressDialog();

        FirebaseMethods.getUrlFromDatabase("feedback_form_url", new GetFeedbackFormUrl() {
            @Override
            public void onCallback(String url) {
                progressDialog.hideProgressDialog();

                Intent intent = new Intent(ProfileActivity.this, WebLinkOpenActivity.class);

                intent.putExtra("url", url);
                intent.putExtra("activity_name", "Feedback");

                startActivity(intent);
            }
        });
    }
}