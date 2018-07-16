package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    /**Data Structures*/
    String UserId; //Stores the user input as a String.
    String PhoneNumber;
    Boolean OTP = false;//This variable will be used to check if the activity is currently in OTP mode or Phone number mode
    private String PhoneVerificationId;

    /**Views*/
    TextView SwitchToEmailTV;
    EditText PhoneOTPTV;
    private ProgressDialog Progress;
    Button SendOTPBTN; //Send OTP Button

    /**Firebase*/
    private FirebaseAuth FbAuth;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        //Linking to views
        SwitchToEmailTV = findViewById(R.id.SwitchToEmailTV);
        SendOTPBTN = findViewById(R.id.SendOTPBTN);
        PhoneOTPTV = findViewById(R.id.PhoneNumberET);

        Progress =new ProgressDialog(this);
        FbAuth = FirebaseAuth.getInstance();

        PhoneOTPTV.setText("+91 ");
        Selection.setSelection(PhoneOTPTV.getText(), PhoneOTPTV.getText().length());

        SendOTPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNumber = PhoneOTPTV.getText().toString().trim().replaceAll(" ", "" );

                if(PhoneNumber.length()==13)
                {
                    if(PhoneNumber.startsWith("+91"))
                    {
                        //OTP will be sent now
                        Progress.setMessage("Sending OTP");
                        Progress.show();
                        sendCode();


                    }
                    else
                    {
                        Toast.makeText(OTPLoginActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
                        PhoneOTPTV.setText("+91 ");
                        Selection.setSelection(PhoneOTPTV.getText(), PhoneOTPTV.getText().length());

                    }
                }
                else
                {
                    Toast.makeText(OTPLoginActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
                    PhoneOTPTV.setText("+91 ");
                    Selection.setSelection(PhoneOTPTV.getText(), PhoneOTPTV.getText().length());
                }
            }
        });




        SwitchToEmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
    public void sendCode() {
        Log.d(LOG_TAG, "Entered sendCode()");


        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
        Log.d(LOG_TAG, "All done. Exiting sendCode()");

    }
    private void setUpVerificationCallbacks() {
        Log.d(LOG_TAG, "Entered setUpVerificationCallbacks()");

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        Log.d(LOG_TAG, "Verification completed successfully");

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.d(LOG_TAG, "Verification Failed");


                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(LOG_TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                            Toast.makeText(OTPLoginActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(LOG_TAG, "SMS Quota exceeded.");
                            Toast.makeText(OTPLoginActivity.this, "Unable to send OTP. Please try logging in using Email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(LOG_TAG, "Entered onCodeSent()");
                        Progress.dismiss();
                        Toast.makeText(OTPLoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        PhoneVerificationId = verificationId;
                        resendToken = token;


                        Intent intent = new Intent(OTPLoginActivity.this, OTPopUp.class);
                        intent.putExtra("PhoneNumber", PhoneNumber);
                        intent.putExtra("phoneVerificationId", PhoneVerificationId);
                        intent.putExtra("resendToken", resendToken);
                        Log.d(LOG_TAG, "All good. Code sent.  Exiting onCodeSent()");
                        startActivity(intent);

                    }
                };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(LOG_TAG, "Entered signInWithPhoneAuthCredential()");


        FbAuth.signInWithCredential(credential)
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
                            Log.d(LOG_TAG, "All good. Exiting signInWithPhoneAuthCredential()");
                            startActivity(i);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTPLoginActivity.this,"Invalid OTP. Try Again.",Toast.LENGTH_SHORT).show();
                                Log.d(LOG_TAG, "Failed. Exiting signInWithPhoneAuthCredential()");

                            }
                        }
                    }
                });
    }


}
