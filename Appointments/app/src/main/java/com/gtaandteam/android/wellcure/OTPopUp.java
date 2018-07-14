package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class OTPopUp extends Activity {
    EditText OTPET;
    Button ResendButton;
    private ProgressDialog progress;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    String PhoneNumber;
    FirebaseAuth fbAuth;
    private String phoneVerificationId;
    final String LOG_TAG = this.getClass().getSimpleName();
    String UserId; //Stores the user input as a String.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpup);
        progress=new ProgressDialog(this);

        OTPET = findViewById(R.id.OTP);
        ResendButton = findViewById(R.id.ResendOTP);
        fbAuth = FirebaseAuth.getInstance();
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        phoneVerificationId = getIntent().getStringExtra("phoneVerificationId");
        resendToken = (PhoneAuthProvider.ForceResendingToken) getIntent().getSerializableExtra("resendToken");
        ResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE)
                {
                    //TODO: Handle resending of OTPs. Function added. Need to test if working.
                    progress.setMessage("Resending OTP");
                    progress.show();
                    resendCode();
                    Toast.makeText(OTPopUp.this, "Resending OTP", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(OTPopUp.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(LOG_TAG, "SMS Quota exceeded.");
                            Toast.makeText(OTPopUp.this, "Unable to send OTP. Please try to Login using EmailET", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        progress.dismiss();
                        Toast.makeText(OTPopUp.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        phoneVerificationId = verificationId;
                        resendToken = token;
                        // ResendOTP.setVisibility(View.VISIBLE); //<------Temporarily commented

                        /*verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        resendButton.setEnabled(true);*/
                    }
                };
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
                            Toast.makeText(OTPopUp.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(getApplicationContext(),DoctorsActivity.class);
                            i.putExtra("loginMode",2);
                            startActivity(i);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPopUp.this,"Invalid OTP. Try Again.",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }
}
