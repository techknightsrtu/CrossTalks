package com.techknightsrtu.crosstalks.models;

public class User {

    private String userId;
    private String originalName;
    private String email;
    private String photoUrl;
    private String gender;
    private String collegeName;
    private String joiningDate;


    public User() {

    }

    public User(String userId, String originalName, String email, String photoUrl,
                String gender, String collegeName, String joiningDate) {
        this.userId = userId;
        this.originalName = originalName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.collegeName = collegeName;
        this.joiningDate = joiningDate;
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

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

}
