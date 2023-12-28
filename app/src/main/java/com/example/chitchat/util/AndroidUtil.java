package com.example.chitchat.util;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.model.UserModel;

public class AndroidUtil {


    public static void showToast(Context context, String Message){
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();

    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFCMToken());



    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel=new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFCMToken(intent.getStringExtra("fcmToken"));

        return userModel;
    }

//    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
//        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
//
//    }


    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (context != null && imageUri != null && imageView != null) {
            try {
                Glide.with(context)
                        .load(imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            } catch (Exception e) {
                // Handle Glide exceptions or any other exceptions here
                e.printStackTrace();
            }
        } else {
            // Handle the case when any of the parameters is null
        }
    }




}
