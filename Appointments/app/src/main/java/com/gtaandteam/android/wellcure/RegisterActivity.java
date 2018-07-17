package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    /**Data Structures*/
    private String PhoneVerificationId;
    private String Password;
    String EmailId;
    String PhoneNumber;
    HashMap<String, String> Data;
    Boolean EmailCredentialCreated, PhoneCredentialCreated, LinkingStatus, PhoneNumberExists;

    /**Views*/
    Button LoginBTN, RegisterBTN;
    EditText UsernameET, PhoneNumberET, PasswordET, ConfirmPasswordET;
    private ProgressDialog Progress;

    /**Firebase*/
    private PhoneAuthProvider.ForceResendingToken ResendToken;
    private FirebaseAuth FbAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    DatabaseReference UserDb1;

    final String LOG_TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Linking to views
        UsernameET =findViewById(R.id.UserNameET);
        PasswordET =findViewById(R.id.PasswordET);
        ConfirmPasswordET = findViewById(R.id.ConfirmPasswordET);
        PhoneNumberET = findViewById(R.id.PhoneNumberET);
        RegisterBTN =findViewById(R.id.RegisterBTN);
        LoginBTN = findViewById(R.id.LoginBTN);

        FbAuth = FirebaseAuth.getInstance();
        PhoneNumberET.setText("+91 ");
        EmailCredentialCreated = false;
        PhoneCredentialCreated = false;
        Progress =new ProgressDialog(this);


        if(FbAuth.getCurrentUser()!=null)
        {
            //user already logged in. go directly to doctor activity
            Toast.makeText(this, "Already signed in with Email : "+FbAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            finish();
            Intent i =new Intent(getApplicationContext(),DoctorsActivity.class);
            i.putExtra("loginMode",0);
            startActivity(i);
        }

        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,EmailLoginActivity.class));

            }
        });

        RegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }
    private void registerUser()
    {
        Log.d(LOG_TAG, "Entered regitserUser()");
        EmailId= UsernameET.getText().toString().trim();
        Password = PasswordET.getText().toString();
        String confirm_pass = ConfirmPasswordET.getText().toString();
        PhoneNumber = PhoneNumberET.getText().toString().trim().replaceAll(" ", "" );

        if(TextUtils.isEmpty(EmailId)){
            // is empty
            Toast.makeText(this,"Please enter Email ID",Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Email ID Entered, Exiting regitserUser()");
            return;
        }

        if(TextUtils.isEmpty(Password)){
            // is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Password Entered, Exiting regitserUser()");
            return;

        }

        if(!TextUtils.equals(Password, confirm_pass)){
            // is empty
            Toast.makeText(this,"Passwords do not match.",Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Passwords do not match, Exiting regitserUser()");

            return;
        }

        if(PhoneNumber.length()==13)
        {
            if(PhoneNumber.startsWith("+91"))
            {
                //OTP will be sent now

                Log.d(LOG_TAG, "Phone Number is good.");
                //sendCode();

            }
            else
            {
                Toast.makeText(RegisterActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
                PhoneNumberET.setText("+91 ");
                Selection.setSelection(PhoneNumberET.getText(), PhoneNumberET.getText().length());
                Log.d(LOG_TAG, "Invalid Phone Number, Exiting regitserUser()");
                return;

            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
            PhoneNumberET.setText("+91 ");
            Selection.setSelection(PhoneNumberET.getText(), PhoneNumberET.getText().length());
            Log.d(LOG_TAG, "Invalid Phone Number, Exiting regitserUser()");
            return;

        }


        // if validations are ok we show a progress bar
            Log.d(LOG_TAG, "All good, Starting Registration");
            Progress.setMessage("Registering Email ID ... ");
            Progress.show();
            FbAuth.createUserWithEmailAndPassword(EmailId, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Progress.dismiss();
                            if (task.isSuccessful()) {
                                //user successfully registered
                                Log.d(LOG_TAG, "Email account successfully created.");
                                Toast.makeText(RegisterActivity.this, "Email registered successfully.", Toast.LENGTH_SHORT).show();
                                //finish();
                                //startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class));

                                Log.d(LOG_TAG, "Proceeding to link Mobile Number with Email ID");
                                Toast.makeText(RegisterActivity.this, "Proceeding to link mobile number with Email ID", Toast.LENGTH_SHORT).show();
                                Progress.setMessage("Sending OTP");
                                Progress.show();
                                PhoneNumberExists =false;
                                checkPhoneNumberExists();
                                if(PhoneNumberExists)
                                {
                                    Log.d(LOG_TAG, "Phone number already registered with another account");
                                    Toast.makeText(RegisterActivity.this, "Phone number already registered with another account.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d(LOG_TAG, "All good, Calling OTP Function sendCode()");
                                    Intent intent = new Intent(RegisterActivity.this, OTPopUp.class);
                                    intent.putExtra("PhoneNumber", PhoneNumber);
                                    intent.putExtra("Parent", LOG_TAG);
                                    Log.d(LOG_TAG, "Done. Exiting setUpVerificationCallbacks() ");
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(RegisterActivity.this, "Couldn't register. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.d(LOG_TAG, "Couldn't create Email Account");
                                //TODO: DISPLAY EXCEPTION MESSAGE AS TO WHY REGISTRATION COULDNT OCCUR. I KNOW THE CODE. WILL ADD IT SOON.
                                //TODO: OK.
                            }
                        }
                    });

            if(EmailCredentialCreated && PhoneCredentialCreated && LinkingStatus)
            {
                Log.d(LOG_TAG, "Calling Register to Database");
                registerToDatabase();
            }

    }
//
//    public void sendCode() {
//        Log.d(LOG_TAG, "Entered sendCode())");
//
//        setUpVerificationCallbacks();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                PhoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                verificationCallbacks);
//    }
//    private void setUpVerificationCallbacks() {
//        Log.d(LOG_TAG, "Entered setUpVerificationCallbacks() ");
//
//
//        verificationCallbacks =
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onVerificationCompleted(
//                            PhoneAuthCredential credential) {
//                        Log.d(LOG_TAG, "Verification Completed Successfully ");
//
//                        PhoneCredentialCreated = true;
//                        linkMobWithEmail(credential);
//
//                        //signInWithPhoneAuthCredential(credential);
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//
//                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                            Log.d(LOG_TAG, "Verification Failed ");
//
//                            // Invalid request
//                            Log.d(LOG_TAG, "Invalid credential: "
//                                    + e.getLocalizedMessage());
//                            Toast.makeText(RegisterActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
//                        } else if (e instanceof FirebaseTooManyRequestsException) {
//                            // SMS quota exceeded
//                            Log.d(LOG_TAG, "SMS Quota exceeded.");
//                            Toast.makeText(RegisterActivity.this, "Unable to send OTP", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCodeSent(String verificationId,
//                                           PhoneAuthProvider.ForceResendingToken token) {
//
//                        Progress.dismiss();
//                        Toast.makeText(RegisterActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
//                        PhoneVerificationId = verificationId;
//                        ResendToken = token;
//
//
//                        Intent intent = new Intent(RegisterActivity.this, OTPopUp.class);
//                        intent.putExtra("PhoneNumber", PhoneNumber);
//                        intent.putExtra("Parent", LOG_TAG);
//                        intent.putExtra("EmailId", EmailId);
//                        intent.putExtra("Password", Password);
//                        Log.d(LOG_TAG, "Done. Exiting setUpVerificationCallbacks() ");
//                        startActivity(intent);
//
//                    }
//                };
//
//    }

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
                Log.v("App","Adding to User Database");
                Toast.makeText(getApplicationContext(),"Stored User Data to UserDatabase",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkPhoneNumberExists()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    PhoneNumberExists =true;
                    String ss = dataSnapshot.getKey().toString();
                    String mPhone,mEmail;
                    mPhone="";
                    mEmail="";
                    if (ss.equals("Phone")) {
                        mPhone = dataSnapshot.getValue().toString();
                    } else if (ss.equals("Email"))
                    {
                        mEmail = dataSnapshot.getValue().toString();
                    }
                    Toast.makeText(RegisterActivity.this, "Phone Number : "+ mPhone +" already linked with Email : "+mEmail, Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Toast.makeText(RegisterActivity.this, "Phone number not linked to any account ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    private void linkMobWithEmail(PhoneAuthCredential credential)
//    {
//        LinkingStatus = false;
//        FbAuth.signInWithEmailAndPassword(EmailId, Password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if(task.isSuccessful())
//                        {
//                            //user successfully logged in
//                            //we start doctor activity here
//                            Toast.makeText(RegisterActivity.this,"Login with New Account Successful",Toast.LENGTH_SHORT).show();
//
//                        }
//                        else
//                        {
//                            Toast.makeText(RegisterActivity.this,"Couldn't Login with new Account ...",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        FbAuth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("App", "linkWithCredential:success");
//                            Toast.makeText(RegisterActivity.this, "Mobile Number has been successfully linked with Email ID", Toast.LENGTH_SHORT).show();
//                            LinkingStatus =true;
//                            //FirebaseUser user = task.getResult().getUser();
//                            //updateUI(user);
//                        } else {
//                            Log.w("App", "linkWithCredential:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            //updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
}

