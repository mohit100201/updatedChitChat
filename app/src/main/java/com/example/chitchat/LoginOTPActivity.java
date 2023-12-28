package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.util.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {
    String PhoneNumber;
    Long TimeOutSeconds=60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken ResendingToken;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOTP;

    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otpactivity);

        PhoneNumber=getIntent().getExtras().getString("phone");
        Toast.makeText(this, PhoneNumber, Toast.LENGTH_SHORT).show();

        otpInput=findViewById(R.id.login_otp);
        nextBtn=findViewById(R.id.login_nextBtn);
        progressBar=findViewById(R.id.login_progress_bar);
        resendOTP=findViewById(R.id.resend_otp_textView);

        sendOtP(PhoneNumber,false);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP=otpInput.getText().toString();
               PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationCode,OTP);
               signIn(credential);
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtP(PhoneNumber,true);
            }
        });







        
    }

    void sendOtP(String phoneNo,boolean isResend){
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder=
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)
                        .setTimeout(TimeOutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed!");
                                setInProgress(false);


                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode=s;
                                ResendingToken=forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP send successfully!");
                                setInProgress(false);

                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }


    }

    private void startResendTimer() {
        resendOTP.setEnabled(false);
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TimeOutSeconds--;
                resendOTP.setText("Resend OTP in " +TimeOutSeconds+" seconds");
                if(TimeOutSeconds<=0){
                    TimeOutSeconds=60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendOTP.setEnabled(true);
                        }
                    });

                }

            }
        },0,1000);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginOTPActivity.this,LoginUsernameActivity.class);
                    intent.putExtra("phone",PhoneNumber);
                    startActivity(intent);


                }
                else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed!");
                }

            }
        });

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);

        }
    }

}