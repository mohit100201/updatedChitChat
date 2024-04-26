package com.example.chitchat;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.adapter.chatRecyclerAdapter;
import com.example.chitchat.adapter.searchUserRecyclerAdapter;
import com.example.chitchat.model.ChatMessageModel;
import com.example.chitchat.model.ChatRoomModel;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatActivity extends AppCompatActivity {
UserModel otherUser;
EditText message_input;
ImageButton sendMessageBtn;
chatRecyclerAdapter adapter;
ImageButton backBtn;
TextView otherUserName;
RecyclerView recyclerView;
String chatroomid;
ChatRoomModel chatRoomModel;



ImageView imageView;

ZegoSendCallInvitationButton voiceCall,videoCall;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser= AndroidUtil.getUserModelFromIntent(getIntent());

        message_input=findViewById(R.id.chat_message_input);
        sendMessageBtn=findViewById(R.id.message_send_btn);
        backBtn=findViewById(R.id.back_btn);
        otherUserName=findViewById(R.id.other_username);
        recyclerView=findViewById(R.id.chat_recycler_view);
        imageView=findViewById(R.id.profile_image_view);
        voiceCall=findViewById(R.id.voiceCall);
        videoCall=findViewById(R.id.videoCall);

        chatroomid= FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());


        // audio and video calling feature

        String userid=FirebaseUtil.currentUserId();
        String otherUserid=otherUser.getUserId();

        if(userid.isEmpty() || otherUserid.isEmpty()){
            return;
        }





        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri=t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);


                    }



                });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Message=message_input.getText().toString().trim();
                if(Message.isEmpty()){
                    return;
                }
                else{
                    SendMessageToUser(Message);
                }

            }



        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        otherUserName.setText(otherUser.getUsername());

        getOrCreateChatRoomModel();
        setUpChatRecyclerView();


        // start service

        startCall(userid);
        setVoiceCall(otherUserid);
        setVideoCall(otherUserid);






    }

    private void startCall(String userID) {
        Application application = getApplication(); // Android's application context
        long appID = 534178145;   // yourAppID
        String appSign ="83ec8a6ea6688ccc20b8ed0f897b065fecda997635c422cf63f3031ce40039eb";  // yourAppSign




        // yourUserName
//        userName=userID;
        String userName=userID;

        DocumentReference docref=FirebaseUtil.currentUserDetails();
        docref.get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot=task.getResult();
                String user= documentSnapshot.toObject(UserModel.class).getUsername().toString();

//                AndroidUtil.showToast(chatActivity.this,user);

                ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

                ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
//                ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
//                notificationConfig.sound = "zego_uikit_sound_call";
//                notificationConfig.channelID = "CallInvitation";
//                notificationConfig.channelName = "CallInvitation";
//                ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, user,callInvitationConfig);
            }




        });










    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

    void setVoiceCall(String otherUserID){
        voiceCall.setIsVideoCall(false);
        voiceCall.setResourceID("zego_uikit_call");
        voiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUserID)));


    }

   void setVideoCall(String otherUserID){
       videoCall.setIsVideoCall(true);
       videoCall.setResourceID("zego_uikit_call");
       videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUserID)));

    }






    private void setUpChatRecyclerView() {
        Query query=FirebaseUtil.getChatRoomMessageReference(chatroomid)
                .orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options=new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter=new chatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }


    void SendMessageToUser(String Message){
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(Message);
        FirebaseUtil.getChatRoomReference(chatroomid).set(chatRoomModel);
        ChatMessageModel chatMessageModel=new ChatMessageModel(Message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatroomid).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            message_input.setText("");
                            sendNotification(Message);

                        }

                    }
                });


    }

    private void sendNotification(String message) {
        // current username,message,current userid,otherUserToken
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserModel currentUser=task.getResult().toObject(UserModel.class);
                    try{
                        JSONObject jsonObject=new JSONObject();

                        JSONObject notificationObj=new JSONObject();
                        notificationObj.put("title",currentUser.getUsername());
                        notificationObj.put("body",message);


                        JSONObject dataObj=new JSONObject();
                        dataObj.put("userId",currentUser.getUserId());

                        jsonObject.put("notification",notificationObj);
                        jsonObject.put("data",dataObj);
                        jsonObject.put("to",otherUser.getFCMToken());
                        callApi(jsonObject);

                    }
                    catch (Exception e){

                    }


                }

            }
        });


    }

    void callApi(JSONObject jsonObject){
         MediaType JSON = MediaType.get("application/json; charset=utf-8");
         OkHttpClient client = new OkHttpClient();
         String URL="https://fcm.googleapis.com/fcm/send";
        RequestBody body=RequestBody.create(jsonObject.toString(),JSON);
        Request request=new Request.Builder()
                .url(URL)
                .post(body)
                .header("Authorization","Bearer AAAAHRITLq8:APA91bF3zrJaGXTLT3EnBezUCTYAs4toHGj-HOn8RywrP1Mk5Se4SspaodWgKfvsyqxxjpn0G1-qWtqLfJS7NAh5J3FVcSR3Ii9tVO7yrgszTOp7sjvoCfU5DpbLsRlFqymbiViyvILp")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });



    }

    private void getOrCreateChatRoomModel() {
        FirebaseUtil.getChatRoomReference(chatroomid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    chatRoomModel=task.getResult().toObject(ChatRoomModel.class);
                    if(chatRoomModel==null){
                        // first time chatting
                        chatRoomModel=new ChatRoomModel(
                                chatroomid,
                                Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                                Timestamp.now(),
                                ""



                        );
                        FirebaseUtil.getChatRoomReference(chatroomid).set(chatRoomModel);
                    }

                }
            }
        });


    }


}



