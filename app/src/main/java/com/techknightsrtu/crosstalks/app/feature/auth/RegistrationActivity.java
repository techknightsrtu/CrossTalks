package com.techknightsrtu.crosstalks.app.feature.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.auth.models.CaptchaResponse;
import com.techknightsrtu.crosstalks.app.feature.auth.retrofit.JsonPlaceHolderApi;
import com.techknightsrtu.crosstalks.app.feature.home.HomeActivity;
import com.techknightsrtu.crosstalks.app.feature.profile.ProfileActivity;
import com.techknightsrtu.crosstalks.app.feature.profile.WebLinkOpenActivity;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.CreateNewUser;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCollegeList;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetCurrentFCMToken;
import com.techknightsrtu.crosstalks.app.helper.ProgressDialog;
import com.techknightsrtu.crosstalks.app.helper.Utility;
import com.techknightsrtu.crosstalks.app.helper.local.UserProfileDataPref;
import com.techknightsrtu.crosstalks.app.models.User;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetFeedbackFormUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.techknightsrtu.crosstalks.app.helper.constants.Avatar.avatarList;
import static com.techknightsrtu.crosstalks.app.helper.constants.Avatar.nameList;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private int genderFlag = -1; // 0 = male, 1 = female
    private int avatarId;
    private String collegeName = "";

    private ArrayList<String> colleges;

    private UserProfileDataPref prefs;

    private TextView tvMale, tvFemale, tvAvatarName, tvCollege, tvUserAgreement;
    private LinearLayout llMale, llFemale;
    private ImageView ivGenerateNewAvatar, ivAvatarImage;
    private ExtendedFloatingActionButton efRegister;

    private ProgressDialog progressDialog;

    // Retrofit
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        generateProfile();
        getCollegesFromDatabase();

        efRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(genderFlag == -1){
                    Toast.makeText(RegistrationActivity.this, "Please Select Your Gender.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(collegeName.equals("")){
                    Toast.makeText(RegistrationActivity.this, "Please Select Your College.", Toast.LENGTH_SHORT).show();
                    return;
                }

                startCaptchaVerification();

            }
        });
    }

    private void startCaptchaVerification() {
        progressDialog.showProgressDialog();

        SafetyNet.getClient(this).verifyWithRecaptcha("6LeZ1uQZAAAAAIvmHvVdJlT0pi_n4GBBdxrJUIKp")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {

                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                            Log.e(TAG, "VALIDATION STEP NEEDED");
                            captchaVerification(userResponseToken);
                        }else {
                            progressDialog.hideProgressDialog();
                            Toast.makeText(RegistrationActivity.this, "Already Checked.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hideProgressDialog();
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
    }

    private void captchaVerification(final String userResponseToken) {

        Call<CaptchaResponse> captchaResponseCall = jsonPlaceHolderApi.getCaptchaResponse(
                "6LeZ1uQZAAAAANh-TpVA1mqwbP8RZ7eSChNlHIbM",
                userResponseToken
        );

        captchaResponseCall.enqueue(new Callback<CaptchaResponse>() {
            @Override
            public void onResponse(Call<CaptchaResponse> call, Response<CaptchaResponse> response) {

                if(!response.isSuccessful()){
                    progressDialog.hideProgressDialog();
                    Toast.makeText(RegistrationActivity.this, "Try Again.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: " + response.message() + " : " + response.code());
                    return;
                }

                if(response.body().isSuccess()){
                    createChatProfile();
                }else{
                    progressDialog.hideProgressDialog();
                    Toast.makeText(RegistrationActivity.this, "Captcha Verification Failed.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CaptchaResponse> call, Throwable t) {
                progressDialog.hideProgressDialog();
                Toast.makeText(RegistrationActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void getCollegesFromDatabase() {
        FirebaseMethods.getCollegesFromDatabase(new GetCollegeList() {
            @Override
            public void onCallback(Map<String, String> collegesList) {
                colleges = new ArrayList<>(collegesList.values());

                //Save College Id's data to local cache
                prefs.setCollegeIdSet(collegesList.keySet());
            }
        });
    }

    private void init() {
        tvMale = findViewById(R.id.tvMale);
        tvFemale = findViewById(R.id.tvFemale);
        tvAvatarName = findViewById(R.id.tvAvatarName);
        tvCollege = findViewById(R.id.tvCollege);

        llMale = findViewById(R.id.llMale);
        llFemale = findViewById(R.id.llFemale);

        ivGenerateNewAvatar = findViewById(R.id.ivGenerateNewAvatar);
        ivAvatarImage = findViewById(R.id.ivUserAvatar);

        efRegister = findViewById(R.id.efRegister);

        progressDialog = new ProgressDialog(RegistrationActivity.this);

        tvUserAgreement = findViewById(R.id.tvUserAgreement);
        SpannableString string = new SpannableString(getString(R.string.user_agreement_text));

        ClickableSpan csTermsOfUse = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                progressDialog.showProgressDialog();

                FirebaseMethods.getUrlFromDatabase("terms_and_conditions", new GetFeedbackFormUrl() {
                    @Override
                    public void onCallback(String url) {
                        progressDialog.hideProgressDialog();

                        Intent intent = new Intent(RegistrationActivity.this, WebLinkOpenActivity.class);

                        intent.putExtra("url", url);

                        startActivity(intent);
                    }
                });
            }
        };

        ClickableSpan csPrivacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                progressDialog.showProgressDialog();

                FirebaseMethods.getUrlFromDatabase("privacy_policy", new GetFeedbackFormUrl() {
                    @Override
                    public void onCallback(String url) {
                        progressDialog.hideProgressDialog();

                        Intent intent = new Intent(RegistrationActivity.this, WebLinkOpenActivity.class);

                        intent.putExtra("url", url);

                        startActivity(intent);
                    }
                });
            }
        };

        string.setSpan(csTermsOfUse, 32, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(csPrivacyPolicy, 53, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvUserAgreement.setText(string);
        tvUserAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        prefs = new UserProfileDataPref(RegistrationActivity.this);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.google.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    public void createChatProfile(){

        prefs.setAvatarId(String.valueOf(avatarId));
        prefs.setJoiningDate(Utility.getCurrentTimestamp());

        createNewUserInDatabase();

    }

    private void createNewUserInDatabase(){
        progressDialog.showProgressDialog();

        FirebaseMethods.getCurrentToken(new GetCurrentFCMToken() {
            @Override
            public void onCallback(String currToken) {

                List<String> tokens = new ArrayList<>();
                tokens.add(currToken);

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
                        originalName,email,photoUrl,gender,collegeId,joiningDate,tokens);

                FirebaseMethods.createNewUserInDatabase(userId, newUser, new CreateNewUser() {
                    @Override
                    public void onCallback(boolean done) {

                        progressDialog.hideProgressDialog();

                        if(done){

                            Intent i = new Intent(RegistrationActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        }else{
                            Log.w(TAG, "Error adding document");
                            Toast.makeText(RegistrationActivity.this, "User not created in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }

    public void chooseCollege(View view) {

        LayoutInflater layoutInflater = LayoutInflater.from(RegistrationActivity.this);
        View ChooseCollegeView = layoutInflater.inflate(R.layout.choose_college_sheet, null);

        EditText etCollegeSearch = ChooseCollegeView.findViewById(R.id.etCollegeSearch);
        ListView lvCollege = ChooseCollegeView.findViewById(R.id.lvCollege);
        TextView tvNoCollegeFound = ChooseCollegeView.findViewById(R.id.tvNoCollegeFound);

        final ArrayAdapter adapter = new ArrayAdapter(
                RegistrationActivity.this,
                R.layout.spinner_item,
                colleges
        );

        lvCollege.setAdapter(adapter);
        lvCollege.setVisibility(View.VISIBLE);

        etCollegeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });

        ChooseCollegeView.setBackgroundColor(Color.TRANSPARENT);
        final Dialog dialog = new BottomSheetDialog(RegistrationActivity.this, R.style.DialogStyle);
        dialog.setContentView(ChooseCollegeView);

        // CODE TO OPEN BOTTOM SHEET IN FULL SCREEN MODE
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override public void onShow(DialogInterface dialogInterface) {
//                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                setupFullHeight(bottomSheetDialog);
//            }
//        });

        ChooseCollegeView.findViewById(R.id.btNotAStudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.setCollegeId("nc");
                prefs.setCollegeName("noCollege");

                dialog.dismiss();
                collegeName = "Not a Student";
                tvCollege.setText("Not a Student");
            }
        });

        ChooseCollegeView.findViewById(R.id.btCollegeNotListed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.showProgressDialog();

                FirebaseMethods.getUrlFromDatabase("add_college_url", new GetFeedbackFormUrl() {
                    @Override
                    public void onCallback(String url) {
                        progressDialog.hideProgressDialog();

                        Intent intent = new Intent(RegistrationActivity.this, WebLinkOpenActivity.class);

                        intent.putExtra("url", url);

                        startActivity(intent);
                    }
                });
            }
        });

        dialog.show();

        lvCollege.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemClick: " + ((TextView) view).getText());
                dialog.dismiss();
                collegeName = "" + ((TextView) view).getText();
                tvCollege.setText(collegeName);

                int position = colleges.indexOf(collegeName);

                Log.d(TAG, "selectCollege: " + collegeName + " : " + position);

                //Extract data from local cache to find out collegeId
                Set<String> collegeIdSet = prefs.getCollegeIdSet();
                ArrayList<String> idList = new ArrayList<>(collegeIdSet);

                String collegeId = idList.get(position);

                //Save user data to local cache
                prefs.setCollegeId(collegeId);
                prefs.setCollegeName(collegeName);
            }
        });

    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (displayMetrics.heightPixels) - 120;
    }

    public void maleClicked(View view) {
        genderFlag = 0;
        prefs.setGender("male");

        llMale.setBackground(getDrawable(R.drawable.gender_clicked));
        llFemale.setBackground(getDrawable(R.drawable.gender_unclicked));

        tvMale.setTextColor(getResources().getColor(R.color.white));
        tvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));

        tvMale.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.ic_male_white),
                null,
                null,
                null
        );
        tvFemale.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.ic_female_red),
                null,
                null,
                null
        );

    }

    public void femaleClicked(View view) {
        genderFlag = 1;
        prefs.setGender("female");

        llMale.setBackground(getDrawable(R.drawable.gender_unclicked));
        llFemale.setBackground(getDrawable(R.drawable.gender_clicked));

        tvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvFemale.setTextColor(getResources().getColor(R.color.white));

        tvMale.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.ic_male_red),
                null,
                null,
                null
        );
        tvFemale.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.ic_female_white),
                null,
                null,
                null
        );

    }

    public void newProfile(View view){
        generateProfile();
    }

    public void generateProfile(){

        // For rotating generate icon
        ivGenerateNewAvatar.startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.rotate));

        // create instance of Random class
        Random rand = new Random();
        avatarId = rand.nextInt(avatarList.size()-1);

        ivAvatarImage.setImageResource(avatarList.get(avatarId));
        tvAvatarName.setText(nameList.get(avatarId));

    }
}