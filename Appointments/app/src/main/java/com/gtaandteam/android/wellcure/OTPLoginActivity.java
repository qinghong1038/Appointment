package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPLoginActivity extends AppCompatActivity {

    TextInputLayout PhoneOTPLayout;
    Button SendOTPButton;
    TextView SwitchToEmail, ResendOTP;
    EditText PhoneOTP;
    Boolean OTP = false;//This variable will be used to check if the activity is currently in OTP mode or Phone number mode
    String EnteredValue; //Stores the user input as a String.
    String PhoneNumber;
    private FirebaseAuth fbAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

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
        fbAuth = FirebaseAuth.getInstance();

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
                char PhoneArray[] = PhoneNumber.toCharArray();


                if(!OTP)
                {
                    /*
                    Currently in Phone number mode.
                    TODO: Handle invalid phone numbers. Done by Glenn. Testing pending.
                    If input is valid, set OTP to True

                    */
                    if(PhoneNumber.length()==13)
                    {
                        if((PhoneArray[0] == '+') && (PhoneArray[1]=='9') && (PhoneArray[2]=='1'))
                        {
                            sendCode();
                            PhoneOTP.setText("");

                            Log.v(LOG_TAG, "Phone Number Accepted");
                            Toast.makeText(OTPLoginActivity.this, "Function to Send OTP", Toast.LENGTH_SHORT).show();

                            OTP = true;
                        }
                        else
                        {
                            Toast.makeText(OTPLoginActivity.this, "Please Enter Your Mobile Number as : +91xxxxxxxxxx", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(OTPLoginActivity.this, "Please Enter Your Mobile Number as : +91xxxxxxxxxx", Toast.LENGTH_LONG).show();
                    }


                }
                else {
                    /*
                    Currently in OTP Mode.
                    TODO: Handle invalid OTPs. Function added. Need to test if working.
                    */
                    verifyCode();
                    Log.v(LOG_TAG, "OTP Sent");

                }

            }
        });


        ResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE)
                {
                    //TODO: Handle resending of OTPs. Function added. Need to test if working.
                    resendCode();
                    Toast.makeText(OTPLoginActivity.this, "Resending OTP", Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG, "Resending OTP");
                }

            }
        });






    }
    public void sendCode() {

        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }
    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(LOG_TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                            Toast.makeText(OTPLoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(LOG_TAG, "SMS Quota exceeded.");
                            Toast.makeText(OTPLoginActivity.this, "Unable to send OTP. Please try to Login using Email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        Toast.makeText(OTPLoginActivity.this, "OTP Sent", Toast.LENGTH_LONG).show();
                        phoneVerificationId = verificationId;
                        resendToken = token;
                        SendOTPButton.setText("Verify OTP");
                        PhoneOTPLayout.setHint("Enter OTP:");
                        ResendOTP.setVisibility(View.VISIBLE);

                        /*verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        resendButton.setEnabled(true);*/
                    }
                };
    }
    public void verifyCode() {

        String code = PhoneOTP.getText().toString().trim();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*signoutButton.setEnabled(true);
                            codeText.setText("");
                            statusText.setText("Signed In");
                            resendButton.setEnabled(true);
                            verifyButton.setEnabled(false);*/
                            /*
                            TODO: Need to check if the registered number is displayed again in TOAST.
                             */
                            FirebaseUser user = task.getResult().getUser();
                            String phone = user.getPhoneNumber();
                            Toast.makeText(OTPLoginActivity.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),DoctorsActivity.class));

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPLoginActivity.this,"Invalid OTP. Try Again.",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

    public void resendCode() {

        //String phoneNumber = phoneText.getText().toString();

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    public void signOut(View view) {
        fbAuth.signOut();
        /*
        TODO: Call this function when signOut is clicked
         */
        /*statusText.setText("Signed Out");
        signoutButton.setEnabled(false);
        sendButton.setEnabled(true);*/
    }
}
