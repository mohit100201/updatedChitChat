package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText username;
    Button letMeIn;
    ProgressBar progressBar;

    String PhoneNumber;

    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        username=findViewById(R.id.login_userName);
        letMeIn=findViewById(R.id.login_let_me_in_btn);
        progressBar=findViewById(R.id.login_progress_bar);
        PhoneNumber=getIntent().getExtras().getString("phone");
        getUserName();

        letMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUsername();
            }
        });




    }

    private void setUsername(){

        String uname=username.getText().toString();
        if(uname.isEmpty() || uname.length()<3){
            username.setError("User name length should be at least 3 char");
            return;
        }

        setInProgress(true);

        if(userModel!=null){
            userModel.setUsername(uname);
        }
        else {
            userModel =new UserModel(PhoneNumber,uname, Timestamp.now(),FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginUsernameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

    }

    private void getUserName() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                userModel=   task.getResult().toObject(UserModel.class);
                 if(userModel!=null){
                     username.setText(userModel.getUsername());

                 }

                }

            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letMeIn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            letMeIn.setVisibility(View.VISIBLE);

        }
    }
}