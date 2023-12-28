package com.example.chitchat.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String userId;

    private String FCMToken;


    public UserModel() {
    }

    private String username;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private Timestamp createdTimeStamp;

    public UserModel(String phone, String username, Timestamp createdTimeStamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimeStamp = createdTimeStamp;
        this.userId=userId;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }
}
