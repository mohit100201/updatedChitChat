package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            // from notification
            String userID=getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        UserModel model=task.getResult().toObject(UserModel.class);

                        Intent MainIntent=new Intent(SplashActivity.this,MainActivity.class);
                        MainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(MainIntent);


                        Intent intent=new Intent(SplashActivity.this, chatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent,model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();

                    }

                }
            });

        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override

                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this,LoginPhoneNumberActivity.class));
                        finish();

                    }



                }
            },2000);
        }




    }
}