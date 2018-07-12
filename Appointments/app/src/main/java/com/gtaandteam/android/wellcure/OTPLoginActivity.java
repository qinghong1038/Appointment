package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Selection;
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
    Boolean OTP = false;//This variable will be used to check if the activity is currently in OTP mode or Phone number mode
    String UserId; //Stores the user input as a String.
    String PhoneNumber;
    private FirebaseAuth fbAuth;
    EditText PhoneOTP;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    Boolean loginMode;
    private ProgressDialog progress;

    final String LOG_TAG = this.getClass().getSimpleName();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);
        progress=new ProgressDialog(this);
        SwitchToEmail = findViewById(R.id.SwitchToEmail);
        PhoneOTPLayout = findViewById(R.id.PhoneOTPLayout);
        SendOTPButton = findViewById(R.id.SendOTP_button);
        ResendOTP = findViewById(R.id.ResendOTP_textView);
        fbAuth = FirebaseAuth.getInstance();

        PhoneOTP = findViewById(R.id.PhoneOTP_editText);
        PhoneOTP.setText("+91 ");
        Selection.setSelection(PhoneOTP.getText(), PhoneOTP.getText().length());

        SwitchToEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class));
                finish();
            }
        });


        SendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneNumber = PhoneOTP.getText().toString().trim().replaceAll(" ", "" );
                Log.v(LOG_TAG, PhoneNumber);

                if(!OTP)
                {
                    /*
                    Currently in Phone number mode.
                    TODO: Handle invalid phone numbers. Done by Glenn. Testing pending.
                    If input is valid, set OTP to True


                    */

                    

                    if(PhoneNumber.length()==13)
                    {
                        if(PhoneNumber.startsWith("+91"))
                        {
                            //OTP will be sent now
                            progress.setMessage("Sending OTP");
                            progress.show();
                            sendCode();
                            PhoneOTP.setText("");
                            Log.v(LOG_TAG, "Phone Number Accepted");
                            Toast.makeText(OTPLoginActivity.this, "Function to Send OTP", Toast.LENGTH_SHORT).show();
                            OTP = true;
                        }
                        else
                        {
                            Toast.makeText(OTPLoginActivity.this, "Please Enter A Valid Phone Number after the +91", Toast.LENGTH_LONG).show();
                            PhoneOTP.setText("+91 ");
                            Selection.setSelection(PhoneOTP.getText(), PhoneOTP.getText().length());

                        }
                    }
                    else
                    {
                        Toast.makeText(OTPLoginActivity.this, "Please Enter A Valid Phone Number after the +91", Toast.LENGTH_LONG).show();
                        PhoneOTP.setText("+91 ");
                        Selection.setSelection(PhoneOTP.getText(), PhoneOTP.getText().length());


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
                    progress.setMessage("Resending OTP");
                    progress.show();
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
                            Toast.makeText(OTPLoginActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(LOG_TAG, "SMS Quota exceeded.");
                            Toast.makeText(OTPLoginActivity.this, "Unable to send OTP. Please try to Login using Email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        progress.dismiss();
                        Toast.makeText(OTPLoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
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

                            FirebaseUser user = task.getResult().getUser();
                            String phone = user.getPhoneNumber();
                            UserId = user.getUid();
                            Toast.makeText(OTPLoginActivity.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(getApplicationContext(),DoctorsActivity.class);
                            i.putExtra("loginMode",2);
                            startActivity(i);

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

    public void signOut() {
        fbAuth.signOut();
        /*
        TODO: Call this function when signOut is clicked. I dont think we need this function here. What say?
         */
        /*statusText.setText("Signed Out");
        signoutButton.setEnabled(false);
        sendButton.setEnabled(true);*/
    }
}
