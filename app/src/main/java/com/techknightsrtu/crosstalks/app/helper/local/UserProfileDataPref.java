package com.techknightsrtu.crosstalks.app.helper.local;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Set;

public class UserProfileDataPref {

    private Activity activity;

    private String userId, avatarId, originalName, email, photoUrl, gender, collegeId, joiningDate, collegeName;

    private Set<String> collegeIdSet;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public UserProfileDataPref(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("AppLocalData",activity.MODE_PRIVATE);


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
        userId = sharedPreferences.getString("userId","");
        return userId;
    }

    public void setUserId(String userId) {
        editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    public String getAvatarId() {
        avatarId = sharedPreferences.getString("avatarId","");
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        editor = sharedPreferences.edit();
        editor.putString("avatarId", avatarId);
        editor.apply();
    }

    public String getOriginalName() {
        originalName = sharedPreferences.getString("originalName","");
        return originalName;
    }

    public void setOriginalName(String originalName) {
        editor = sharedPreferences.edit();
        editor.putString("originalName", originalName);
        editor.apply();
    }

    public String getEmail() {
        email = sharedPreferences.getString("email","");
        return email;
    }

    public void setEmail(String email) {
        editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getPhotoUrl() {
        photoUrl = sharedPreferences.getString("photoUrl","");
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        editor = sharedPreferences.edit();
        editor.putString("photoUrl", photoUrl);
        editor.apply();
    }

    public String getGender() {
        gender = sharedPreferences.getString("gender","");
        return gender;
    }

    public void setGender(String gender) {
        editor = sharedPreferences.edit();
        editor.putString("gender", gender);
        editor.apply();
    }

    public String getCollegeId() {
        collegeId = sharedPreferences.getString("collegeId","");
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        editor = sharedPreferences.edit();
        editor.putString("collegeId", collegeId);
        editor.apply();
    }

    public String getJoiningDate() {
        joiningDate = sharedPreferences.getString("joiningDate","");
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        editor = sharedPreferences.edit();
        editor.putString("joiningDate", joiningDate);
        editor.apply();
    }

    public String getCollegeName() {
        collegeName = sharedPreferences.getString("collegeName","");
        if(collegeName.equals("noCollege"))
            return "";
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        editor = sharedPreferences.edit();
        editor.putString("collegeName", collegeName);
        editor.apply();
    }

    public Set<String> getCollegeIdSet() {
        collegeIdSet = sharedPreferences.getStringSet("collegeIds",null);
        return collegeIdSet;
    }

    public void setCollegeIdSet(Set<String> collegeIdSet) {
        editor = sharedPreferences.edit();
        editor.putStringSet("collegeIds", collegeIdSet);
        editor.apply();
    }
}
