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
import com.example.chitchat.model.ChatRoomModel;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
    FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                         boolean lastMessageSendByMe=model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        UserModel otherUserModel=task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri=t.getResult();
                                        AndroidUtil.setProfilePic(context,uri,holder.profileView);


                                    }



                                });

                        holder.usernameText.setText(otherUserModel.getUsername());
                        if(lastMessageSendByMe){
                            holder.lastMessageText.setText("You: "+model.getLastMessage());
                        }
                        else{
                            holder.lastMessageText.setText(model.getLastMessage());
                        }

                        holder.lastMessageTime.setText(FirebaseUtil.timeStampToString(model.getLastMessageTimeStamp()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //navigate to chat activity
                                Intent intent=new Intent(context, chatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });

                    }

                }
            });




    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);

        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText,lastMessageTime;
        ImageView profileView;


        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText=itemView.findViewById(R.id.username_text);
            lastMessageText=itemView.findViewById(R.id.last_message_text);
            lastMessageTime=itemView.findViewById(R.id.last_message_time_text);
            profileView=itemView.findViewById((R.id.profile_pic_image_view));

        }
    }


}
