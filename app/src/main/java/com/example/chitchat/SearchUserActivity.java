package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chitchat.adapter.searchUserRecyclerAdapter;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    searchUserRecyclerAdapter  adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);


        searchInput=findViewById(R.id.search_user_input);
        searchButton=findViewById(R.id.search_user_btn);
        backButton=findViewById(R.id.back_btn);
        recyclerView=findViewById(R.id.search_user_recycler_view);
        // keyboard open
        searchInput.requestFocus();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=searchInput.getText().toString();
                if(user.isEmpty() || user.length()<3){
                    searchInput.setError("Invalid Username");
                    return;

                }

                setupRecyclerView(user);


            }
        });




    }

    private void setupRecyclerView(String user) {
        Query query=FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",user)
                .whereLessThanOrEqualTo("username",user+'\uf8ff');
        FirestoreRecyclerOptions<UserModel> options=new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter=new searchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }
}