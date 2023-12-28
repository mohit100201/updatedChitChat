package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.LoginOTPActivity;
import com.example.chitchat.R;
import com.example.chitchat.SearchUserActivity;
import com.example.chitchat.SplashActivity;
import com.example.chitchat.chatActivity;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class searchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, searchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    public searchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());

        if(model.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.usernameText.setText(model.getUsername() + "(Me)");
        }

        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri=t.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.profileView);


                    }



                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to chat activity
                Intent intent=new Intent(context, chatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);

        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView phoneText;
        ImageView profileView;


        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText=itemView.findViewById(R.id.username_text);
            phoneText=itemView.findViewById(R.id.phone_text);
            profileView=itemView.findViewById((R.id.profile_pic_image_view));

        }
    }


}
