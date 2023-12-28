package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText PhoneNumber;
    Button sendOtP;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker=findViewById(R.id.login_countryCode);
        PhoneNumber=findViewById(R.id.login_mobileNumber);
        sendOtP=findViewById(R.id.send_otp_btn);
        progressBar=findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(PhoneNumber);
        sendOtP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countryCodePicker.isValidFullNumber()){
                    Intent intent=new Intent(LoginPhoneNumberActivity.this,LoginOTPActivity.class);
                    intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);

                }
                else{
                    PhoneNumber.setError("Phone Number is not valid !");
                }
            }
        });


    }
}