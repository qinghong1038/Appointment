package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OTPLoginActivity extends AppCompatActivity {

    TextInputLayout PhoneOTPLayout;
    Button SendOTPButton;
    TextView SwitchToEmail, ResendOTP;
    EditText PhoneOTP;
    Boolean OTP = false;//This variable will be used to check if the activity is currently in OTP mode or Phone number mode
    String EnteredValue; //Stores the user input as a String.
    String PhoneNumber;
    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        SwitchToEmail = findViewById(R.id.SwitchToEmail);
        PhoneOTPLayout = findViewById(R.id.PhoneOTPLayout);
        PhoneOTP = findViewById(R.id.PhoneOTP_editText);
        SendOTPButton = findViewById(R.id.SendOTP_button);
        ResendOTP = findViewById(R.id.ResendOTP_textView);

        SwitchToEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


        SendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneNumber = PhoneOTP.getText().toString().trim();
                PhoneOTP.setText("");

                if(!OTP)
                {
                    /*
                    Currently in Phone number mode.
                    TODO: Handle invalid phone numbers
                    If input is valid, set OTP to True

                    */
                    SendOTPButton.setText("Verify OTP");
                    PhoneOTPLayout.setHint("Enter OTP:");
                    Log.v(LOG_TAG, "Phone number accepted");
                    ResendOTP.setVisibility(View.VISIBLE);
                    OTP = true;

                }
                else {
                    /*
                    Currently in OTP Mode.
                    TODO: Handle invalid OTPs
                    */
                    Log.v(LOG_TAG, "OTP Sent");

                }

            }
        });


        ResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE)
                {
                    //TODO: Handle resending of OTPs.
                    Log.v(LOG_TAG, "Resending OTP");
                }

            }
        });






    }
}
