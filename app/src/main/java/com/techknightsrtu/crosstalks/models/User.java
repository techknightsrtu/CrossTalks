package com.techknightsrtu.crosstalks.models;

import java.util.List;

public class User {

    private String userId;
    private String avatarId;
    private String originalName;
    private String email;
    private String photoUrl;
    private String gender;
    private String collegeId;
    private String joiningDate;
    private List<String> tokens;



    public User() {

    }

    public User(String userId, String avatarId, String originalName, String email, String photoUrl,
                String gender, String collegeId, String joiningDate,List<String> tokens) {
        this.userId = userId;
        this.avatarId = avatarId;
        this.originalName = originalName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.collegeId = collegeId;
        this.joiningDate = joiningDate;
        this.tokens = tokens;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", avatarId='" + avatarId + '\'' +
                ", originalName='" + originalName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", gender='" + gender + '\'' +
                ", collegeId='" + collegeId + '\'' +
                ", joiningDate='" + joiningDate + '\'' +
                '}';
    }
}
