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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    Button LoginButton,
            RegisterButton;
    
    EditText UsernameET,
            PhoneNumberET,
            PasswordET,
            ConfirmPasswordET;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneVerificationId;
    String UserId;
    private ProgressDialog progress;
    private FirebaseAuth FbAuth;
    String PhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    final String LOG_TAG = this.getClass().getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UsernameET =findViewById(R.id.UserNameET);
        PasswordET =findViewById(R.id.PasswordET);
        ConfirmPasswordET = findViewById(R.id.ConfirmPasswordET);
        PhoneNumberET = findViewById(R.id.PhoneNumberET);
        progress =new ProgressDialog(this);
        RegisterButton =findViewById(R.id.reg_button);
        FbAuth = FirebaseAuth.getInstance();
        PhoneNumberET.setText("+91 ");

        if(FbAuth.getCurrentUser()!=null)
        {
            //user already logged in. go directly to doctor activity
            finish();
            Intent i =new Intent(getApplicationContext(),DoctorsActivity.class);
            i.putExtra("loginMode",0);
            startActivity(i);
        }
        LoginButton = findViewById(R.id.login_button2);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,EmailLoginActivity.class));

            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }
    private void registerUser()
    {
        String email= UsernameET.getText().toString().trim();
        String pass= PasswordET.getText().toString();
        String confirm_pass = ConfirmPasswordET.getText().toString();
        PhoneNumber = PhoneNumberET.getText().toString().trim().replaceAll(" ", "" );

        if(TextUtils.isEmpty(email)){
            // is empty
            Toast.makeText(this,"Please Enter Email Id",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            // is empty
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;

        }

        if(!TextUtils.equals(pass, confirm_pass)){
            // is empty
            Toast.makeText(this,"Passwords do not match.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(PhoneNumber.length()==13)
        {
            if(PhoneNumber.startsWith("+91"))
            {
                //OTP will be sent now
                progress.setMessage("Sending OTP");
                progress.show();
                sendCode();


            }
            else
            {
                Toast.makeText(RegisterActivity.this, "Please Enter A Valid Phone Number after the +91", Toast.LENGTH_LONG).show();
                PhoneNumberET.setText("+91 ");
                Selection.setSelection(PhoneNumberET.getText(), PhoneNumberET.getText().length());

            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Please Enter A Valid Phone Number after the +91", Toast.LENGTH_LONG).show();
            PhoneNumberET.setText("+91 ");
            Selection.setSelection(PhoneNumberET.getText(), PhoneNumberET.getText().length());
            return;

        }


        // if validations are ok we show a progress bar

            progress.setMessage("Registering User..");
            progress.show();
            FbAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progress.dismiss();
                            if (task.isSuccessful()) {
                                //user successfully registered
                                //we start profile activity here
                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class));


                            } else {
                                Toast.makeText(RegisterActivity.this, "Couldnt Register. Please Try Again..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            progress.setMessage("Sending OTP");
            progress.show();
            sendCode();
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
                            Toast.makeText(RegisterActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(LOG_TAG, "SMS Quota exceeded.");
                            Toast.makeText(RegisterActivity.this, "Unable to send OTP", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        progress.dismiss();
                        Toast.makeText(RegisterActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        phoneVerificationId = verificationId;
                        resendToken = token;


                        Intent intent = new Intent(RegisterActivity.this, OTPopUp.class);
                        intent.putExtra("PhoneNumber", PhoneNumber);
                        intent.putExtra("phoneVerificationId", phoneVerificationId);
                        intent.putExtra("resendToken", resendToken);

                        startActivity(intent);

                    }
                };
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
                            Toast.makeText(RegisterActivity.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(getApplicationContext(),DoctorsActivity.class);
                            i.putExtra("loginMode",2);
                            startActivity(i);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(RegisterActivity.this,"Invalid OTP. Try Again.",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

}
