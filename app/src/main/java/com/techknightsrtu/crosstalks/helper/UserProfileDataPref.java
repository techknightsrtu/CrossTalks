package com.techknightsrtu.crosstalks.helper;

import android.app.Activity;
import android.content.SharedPreferences;

public class UserProfileDataPref {

    private Activity activity;

    private String userId, avatarId, originalName, email, photoUrl, gender, collegeId, joiningDate, collegeName;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserProfileDataPref(){

    }

    public UserProfileDataPref(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("AppLocalData",activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userId = sharedPreferences.getString("userId","");
        avatarId = sharedPreferences.getString("avatarId","");
        originalName = sharedPreferences.getString("originalName","");
        email = sharedPreferences.getString("email","");
        photoUrl = sharedPreferences.getString("photoUrl","");
        gender = sharedPreferences.getString("gender","");
        collegeId = sharedPreferences.getString("collegeId","");
        joiningDate = sharedPreferences.getString("joiningDate","");
        collegeName = sharedPreferences.getString("collegeName","");
    }

    public UserProfileDataPref(Activity activity, String userId, String avatarId, String originalName, String email,
                               String photoUrl, String gender, String collegeId, String joiningDate, String collegeName) {
        this.activity = activity;
        this.userId = userId;
        this.avatarId = avatarId;
        this.originalName = originalName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.collegeId = collegeId;
        this.joiningDate = joiningDate;
        this.collegeName = collegeName;

        sharedPreferences = activity.getSharedPreferences("AppLocalData",activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("userId", userId);
        editor.putString("avatarId", avatarId);
        editor.putString("originalName", originalName);
        editor.putString("email", email);
        editor.putString("photoUrl", photoUrl);
        editor.putString("gender", gender);
        editor.putString("collegeId", collegeId);
        editor.putString("joiningDate", joiningDate);
        editor.putString("collegeName", collegeName);
        editor.apply();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        editor.putString("userId", userId);
        editor.apply();
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        editor.putString("avatarId", avatarId);
        editor.apply();
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        editor.putString("originalName", originalName);
        editor.apply();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.apply();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        editor.putString("photoUrl", photoUrl);
        editor.apply();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        editor.putString("gender", gender);
        editor.apply();
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        editor.putString("collegeId", collegeId);
        editor.apply();
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        editor.putString("joiningDate", joiningDate);
        editor.apply();
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        editor.putString("collegeName", collegeName);
        editor.apply();
    }
}
