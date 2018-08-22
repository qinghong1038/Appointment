package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPopUp extends Activity {

    /**Data Structures*/
    String UserId; //Stores the user input as a String.
    private String phoneVerificationId;
    String PhoneNumber;
    String Parent;
    String OTP,Code;
    Boolean PhoneCredentialCreated, LinkingStatus,VerificationCompleted,AutoEnteredOTP,OTPReceived,EmailCreated;
    String EmailId;
    private String Password;
    HashMap<String, String> Data;
    int RetryCount=0;



    /**Views*/
    EditText OTPET; // OTP EditText
    Button ResendBTN, LoginBTN;
    static ProgressDialog Progress;
    AlertDialog.Builder Pbuilder;
    static ProgressDialog VerifyProgress;


    /**Firebase*/
    FirebaseAuth FbAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken ResendToken;
    DatabaseReference UserDb1;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        setContentView(R.layout.activity_otpup);

        //Linking to views
        OTPET = findViewById(R.id.OTPBTN);
        ResendBTN = findViewById(R.id.ResendOTPBTN);
        LoginBTN = findViewById(R.id.LoginBTN);
        Progress =new ProgressDialog(this);
        VerifyProgress = new ProgressDialog(this);
        Parent = getIntent().getStringExtra("Parent");
        Log.d(LOG_TAG,  "Parent Activity: " + Parent);
        if(!Parent.equals("OTPLoginActivity"))
        {
            RegisterActivity.Progress.dismiss();
        }
        //Log.d(LOG_TAG),"Width Major : "
        try {
            if(ProfileActivity.Progress2.isShowing())
                ProfileActivity.Progress2.dismiss();
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG,"No Such Progress : "+e.getMessage());
        }
        VerificationCompleted = false;
        OTPReceived=false;
        AutoEnteredOTP=false;
        LinkingStatus = false;
        EmailCreated=false;
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
                    Progress.setCancelable(false);
                    Progress.show();
                    timerDelayRemoveDialog(30000,Progress);
                    if(!AutoEnteredOTP)
                        OTPET.setText("");
                    hideKeyboard(OTPopUp.this);
                    verifyCode();
                }

            }
        });
        Pbuilder = new AlertDialog.Builder(this);
        Pbuilder.setCancelable(false);

        if(Parent.equals("RegisterActivity"))
        {    Pbuilder.setTitle("Register with Email Only?");
            Pbuilder.setMessage("Would you like to continue anyway without linking mobile number?\n\n" +
                    "Note : If you proceed without linking mobile number, you wont be able to Login using Mobile Number");
        }
        else if(Parent.equals("OTPLoginActivity"))
        {
            Pbuilder.setTitle("Waiting for OTP");
            Pbuilder.setMessage("Would you like to Login with Email Instead?\n\n" +
                    "If not, You can continue to wait for the OTP");
        }
        else
        {   Pbuilder.setTitle("Skip Verifying Mobile Number?");
            Pbuilder.setMessage("Would you like to continue anyway without linking mobile number?\n\n" +
                    "Note : If you proceed without linking mobile number, you won't be able to Login using Mobile Number");
        }

        Pbuilder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(LOG_TAG,"Proceed Without Verifying OTP");
                        if(!Parent.equals("ProfileActivity"))
                        {
                            FbAuth.signOut();
                            Intent intent = new Intent(OTPopUp.this, WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(OTPopUp.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
        Pbuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(OTPopUp.this, "Retrying Phone Verification", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG,"Retrying Phone Verification");
                RetryCount=0;
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        AlertDialog rdialog = Pbuilder.create();
                        try
                        {
                            rdialog.show();
                        }
                        catch (Exception e)
                        {}

                    }

                }, 20000);
            }
        });
        FbAuth = FirebaseAuth.getInstance();
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");

        ResendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE)
                {
                    Progress.setMessage("Resending OTP");
                    Progress.setCancelable(false);
                    Progress.show();
                   // timerDelayRemoveDialog(10000,Progress);
                    resendCode();
                    Toast.makeText(OTPopUp.this, "Resending OTP", Toast.LENGTH_LONG).show();
                }

            }
        });
//
        Progress.setMessage("Sending OTP");
        Progress.setCancelable(false);
        Progress.show();
        timerDelayRemoveDialog(30000,Progress);
        sendCode();
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if(!VerificationCompleted)
                {
                    try {
                        AlertDialog dialog = Pbuilder.create();
                        dialog.show();
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG,""+e.getMessage());
                    }

                }


            }

        }, 30000);
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
        Log.d(LOG_TAG,"Inside VerifyCode()");
        VerificationCompleted=true;
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
                        Log.d(LOG_TAG,"Inside onVerification Completed");
                        Progress.setMessage("Auto Detecting OTP");
                        Progress.show();
                        VerificationCompleted=true;
                        AutoEnteredOTP=true;
                        final PhoneAuthCredential pcredential = credential;
                        Progress.dismiss();
                        OTPET.setText(pcredential.getSmsCode());
                        Log.d(LOG_TAG,pcredential.getSmsCode());

                        Log.d(LOG_TAG, "Auto Verification Completed Successfully");


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
                        Log.d(LOG_TAG, "Will Try Auto Retrieving OTP");




                        Log.d(LOG_TAG,"This log was not delayed");
                        //Toast.makeText(OTPopUp.this, "This toast was not delayed", Toast.LENGTH_SHORT).show();

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

                            Answers.getInstance().logLogin(new LoginEvent()
                                    .putMethod("Digits")
                                    .putSuccess(true));
                            FirebaseUser user = task.getResult().getUser();
                            String phone = user.getPhoneNumber();
                            UserId = user.getUid();
                            Progress.dismiss();
                            Toast.makeText(OTPopUp.this,"Login Successful by : "+phone,Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(getApplicationContext(),DoctorsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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


        if(Parent.equals("RegisterActivity")){
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
                                EmailCreated=true;

                            }
                            else
                            {
                                Toast.makeText(OTPopUp.this,"Couldn't Login with new Account ...",Toast.LENGTH_SHORT).show();
                                Log.d(LOG_TAG,"Couldnt Login before linking. Error : "+task.getException().getMessage());
                                return;
                            }
                        }
                    });
        }
        FbAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Progress.dismiss();
                            Log.d(LOG_TAG, "linkWithCredential:success");
                            Toast.makeText(OTPopUp.this, "Mobile Number has been successfully linked with Email ID", Toast.LENGTH_SHORT).show();

                            Log.d(LOG_TAG, "Calling Register to Database");
                            registerToDatabase();
                            //FirebaseUser user = task.getResult().getUser();
                            //updateUI(user);
                        } else {
                            Log.w(LOG_TAG, "linkWithCredential:failure", task.getException());
                            Progress.dismiss();
                            Toast.makeText(OTPopUp.this, "Authentication Failed.\n"+task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            OTPET.setText("");
                            RetryCount++;
                            if(RetryCount==3)
                            {
                                AlertDialog dialog = Pbuilder.create();
                                dialog.show();
                            }


                        }


                    }
                });
    }

    private void registerToDatabase()
    {
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("Email and Mobile")
                .putSuccess(true));
        Data = new HashMap<>();
        Data.put("Email", EmailId);
        Data.put("Phone", PhoneNumber);
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("mobileDB");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //finish();
                //go to page which shows users details
                if(task.isSuccessful())
                {
                    LinkingStatus=true;
                    Log.v("App","Adding to User Database");
                    Toast.makeText(getApplicationContext(),"Stored User Data to UserDatabase",Toast.LENGTH_SHORT).show();
                    if(!Parent.equals("ProfileActivity"))
                    {
                        Toast.makeText(OTPopUp.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                        FbAuth.signOut();
                        Intent intent = new Intent(OTPopUp.this, WelcomeActivity.class);
                        intent.putExtra("Parent", LOG_TAG);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(OTPopUp.this, ProfileActivity.class);
                        intent.putExtra("LinkingStatus",true);
                        intent.putExtra("Parent", LOG_TAG);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }


                }

            }
        });
    }
    /*protected void onStop() {

        super.onStop();
        Toast.makeText(this, "Closing App", Toast.LENGTH_SHORT).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(!Parent.equals("OTPLoginActivity"))
        {
            if(user!=null)
            {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(LOG_TAG, "User account deleted.");
                                }
                            }
                        });
            }
        }

    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((Parent.equals("RegisterActivity"))&&(!LinkingStatus)&&(keyCode == KeyEvent.KEYCODE_BACK)){
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(OTPopUp.this, R.style.AlertDialogCustom));
            builder.setCancelable(true);
            builder.setTitle("Exit Registration");
            builder.setMessage("Are you sure you want to Cancel Registration and Delete The Email Account Created?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG, "Delete Email User");
                            if(EmailCreated)
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null)
                                {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(LOG_TAG, "User account deleted.");
                                                    }
                                                }
                                            });
                                }
                            }
                            Intent intent = new Intent(OTPopUp.this, WelcomeActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                        }
                    });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(OTPopUp.this, R.style.AlertDialogCustom));
                    builder.setCancelable(true);
                    builder.setTitle("Continue With Registration");
                    builder.setMessage("Would you like to Continue Waiting for Registration Completion?");
                    builder.setPositiveButton("YES, Continue",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.setNegativeButton("NO, Keep Email Account Only", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(FbAuth.getCurrentUser()!=null)
                                FbAuth.signOut();
                            Intent intent = new Intent(OTPopUp.this, WelcomeActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return super.onKeyDown(keyCode, event);
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Acitivites Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(OTPopUp.this, R.style.AlertDialogCustom));
                builder.setCancelable(true);
                builder.setTitle("Exit App");
                builder.setMessage("Are you sure you want to Exit?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(LOG_TAG, "Exiting App");
                                finishAffinity();

                            }
                        });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Log.d(LOG_TAG, "Other Activites Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(OTPopUp.this, R.style.AlertDialogCustom));
                builder.setCancelable(true);
                builder.setTitle("Try Different Login Method");
                builder.setMessage("Would You Like To Stop and Try a Different Login Method?");
                builder.setPositiveButton("YES, Go to Home Screen",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(OTPopUp.this, WelcomeActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        });
                builder.setNegativeButton("NO, Continue Waiting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_DEL))
            Log.d(LOG_TAG,"Backspace Pressed");
        return super.onKeyDown(keyCode, event);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Log.d("OTPopUp","Keyboard Closed");
    }
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(OTPopUp.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,""+e.getMessage());
                }
            }
        }, time);
    }

}
