package com.example.chitchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    ProgressBar progressBar;
    Button updateBtn;
    TextView logOutBtn;

    UserModel currentUserModel;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent>imagePickLauncher;




    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if(result.getResultCode()== Activity.RESULT_OK){
                Intent data=result.getData();
                if(data!=null && data.getData()!=null){
                    selectedImageUri=data.getData();
                    AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);


                }
            }

                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic=view.findViewById(R.id.profile_image_view);
        usernameInput=view.findViewById(R.id.profile_username);
        phoneInput=view.findViewById(R.id.profile_phone);
        progressBar=view.findViewById(R.id.profile_progress_bar);
        updateBtn=view.findViewById(R.id.profile_update_btn);
        logOutBtn=view.findViewById(R.id.logOutBtn);

        getUserData();

        updateBtn.setOnClickListener(v->{
        updateBtnClick();




        });

        logOutBtn.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseUtil.logOut();
                        Intent intent=new Intent(getContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }

                }
            });



        });

       profilePic.setOnClickListener(v -> {

           ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                   .createIntent(new Function1<Intent, Unit>() {
                       @Override
                       public Unit invoke(Intent intent) {
                           imagePickLauncher.launch(intent);
                           return null;
                       }
                   });



       });



        return view;
    }

    void updateBtnClick(){
        String newUsername=usernameInput.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            usernameInput.setError("User name length should be at least 3 char");

            return;
        }

        currentUserModel.setUsername(newUsername);
        setInProgress(true);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            updateToFireStore();
                        }
                    });

        }
        else{
            updateToFireStore();
        }





    }

    void updateToFireStore(){

        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInProgress(false);
                        if(task.isSuccessful()){
                            AndroidUtil.showToast(getContext(),"Update Successfully");

                        }
                        else{
                            AndroidUtil.showToast(getContext(),"Update failed");

                        }

                    }
                });

    }

    void getUserData(){
        setInProgress(true);
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri=task.getResult();
                                AndroidUtil.setProfilePic(getContext(),uri,profilePic);


                            }



                        });


        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                currentUserModel =task.getResult().toObject(UserModel.class);
                usernameInput.setText(currentUserModel.getUsername());
                phoneInput.setText(currentUserModel.getPhone());




            }
        });



    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);

        }
    }
}