package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPopUp extends Activity {

    /**Data Structures*/
    String UserId; //Stores the user input as a String.
    private String phoneVerificationId;
    String PhoneNumber;
    String Parent;
    String OTP,Code;
    Boolean PhoneCredentialCreated, LinkingStatus;
    String EmailId;
    private String Password;
    HashMap<String, String> Data;




    /**Views*/
    EditText OTPET; // OTP EditText
    Button ResendBTN, LoginBTN;
    private ProgressDialog Progress;


    /**Firebase*/
    FirebaseAuth FbAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken ResendToken;
    DatabaseReference UserDb1;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpup);

        //Linking to views
        OTPET = findViewById(R.id.OTPBTN);
        ResendBTN = findViewById(R.id.ResendOTPBTN);
        LoginBTN = findViewById(R.id.LoginBTN);
        Progress =new ProgressDialog(this);


        OTPET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(LOG_TAG, "onTextChangedOTP");


            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(LOG_TAG, "afterTextChangedOTP");
                OTP = OTPET.getText().toString();
                if(OTP.length() == 6)
                {
                    Code=OTP;
                    Progress.setMessage("Verifying OTP");
                    Progress.show();
                    OTPET.setText("");
                    verifyCode();
                }

            }
        });

        FbAuth = FirebaseAuth.getInstance();
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        Parent = getIntent().getStringExtra("Parent");
        Log.d(LOG_TAG,  "Parent ativity: " + Parent);

        ResendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE)
                {
                    Progress.setMessage("Resending OTP");
                    Progress.show();
                    resendCode();
                    Toast.makeText(OTPopUp.this, "Resending OTP", Toast.LENGTH_LONG).show();
                }

            }
        });
//
        Progress.setMessage("Sending OTP");
        Progress.show();
        sendCode();
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

    public void verifyCode() {

        Log.d(LOG_TAG, "Displaying OTP Value :"+Code);
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, Code);
        Log.d(LOG_TAG, "Verification In Progress");
        if(Parent.equals("OTPLoginActivity"))
            signInWithPhoneAuthCredential(credential);
        else
        {
            PhoneCredentialCreated = true;
            linkMobWithEmail(credential);
        }
    }

    public void resendCode() {
        Log.d(LOG_TAG, "Entered resendCode() ");


        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                ResendToken);

        Log.d(LOG_TAG, "Done. Exiting Completed ");

    }

    private void setUpVerificationCallbacks() {
        Log.d(LOG_TAG, "Entered setUpVerificationCallbacks() ");


        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        Log.d(LOG_TAG, "Verification Completed Successfully");
                        if(Parent.equals("OTPLoginActivity"))
                            signInWithPhoneAuthCredential(credential);
                        else
                        {
                            PhoneCredentialCreated = true;
                            linkMobWithEmail(credential);
                        }

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(LOG_TAG, "Verification Failed");

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
                        Log.d(LOG_TAG, "Entered onCodeSent() ");


                        phoneVerificationId = verificationId;
                        ResendToken = token;
                        Log.d(LOG_TAG, "All good. Code sent. Exiting onCodeSent() ");
                        Toast.makeText(OTPopUp.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        Progress.dismiss();


                    }
                };

        Log.d(LOG_TAG, "Done. Exiting setUpVerificationCallbacks() ");

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            String phone = user.getPhoneNumber();
                            UserId = user.getUid();
                            Progress.dismiss();
                            Toast.makeText(OTPopUp.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(getApplicationContext(),DoctorsActivity.class);
                            i.putExtra("loginMode",2);
                            startActivity(i);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Progress.dismiss();
                                Toast.makeText(OTPopUp.this,"Invalid OTP. Try Again.",Toast.LENGTH_SHORT).show();
                                OTPET.setText("");

                            }
                        }
                    }
                });
    }

    private void linkMobWithEmail(PhoneAuthCredential credential)
    {
        LinkingStatus = false;
        EmailId = getIntent().getStringExtra("EmailId");
        Password = getIntent().getStringExtra("Password");

        FbAuth.signInWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            //user successfully logged in
                            //we start doctor activity here
                            Toast.makeText(OTPopUp.this,"Login with New Account Successful",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(OTPopUp.this,"Couldn't Login with new Account ...",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        FbAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Progress.dismiss();
                            Log.d(LOG_TAG, "linkWithCredential:success");
                            Toast.makeText(OTPopUp.this, "Mobile Number has been successfully linked with Email ID", Toast.LENGTH_SHORT).show();

                            Log.d(LOG_TAG, "Calling Register to Database");
                            registerToDatabase();
                            //FirebaseUser user = task.getResult().getUser();
                            //updateUI(user);
                        } else {
                            Log.w("App", "linkWithCredential:failure", task.getException());
                            Progress.dismiss();
                            Toast.makeText(OTPopUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            OTPET.setText("");
                        }

                        // ...
                    }
                });
    }

    private void registerToDatabase()
    {
        Data = new HashMap<>();
        Data.put("Email", EmailId);
        Data.put("Phone", PhoneNumber);
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("userDB");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //finish();
                //go to page which shows users details
                if(task.isSuccessful())
                {
                    Log.v("App","Adding to User Database");
                    Toast.makeText(getApplicationContext(),"Stored User Data to UserDatabase",Toast.LENGTH_SHORT).show();
                    FbAuth.signOut();
                    startActivity(new Intent(OTPopUp.this, EmailLoginActivity.class));
                }

            }
        });
    }
}
