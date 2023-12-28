package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.LoginOTPActivity;
import com.example.chitchat.R;
import com.example.chitchat.SearchUserActivity;
import com.example.chitchat.SplashActivity;
import com.example.chitchat.chatActivity;
import com.example.chitchat.model.ChatMessageModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class chatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, chatRecyclerAdapter.chatModelViewHolder> {

    Context context;
    public chatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull chatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
        }
        else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setText(model.getMessage());

        }
        



    }

    @NonNull
    @Override
    public chatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);

        return new chatModelViewHolder(view);
    }

    class chatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextView,rightChatTextView;


        public chatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout=itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout=itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView=itemView.findViewById(R.id.left_chat_txtView);
            rightChatTextView=itemView.findViewById(R.id.right_chat_txtView);


          

        }
    }


}
