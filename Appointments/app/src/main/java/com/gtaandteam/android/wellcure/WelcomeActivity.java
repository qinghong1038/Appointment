package com.gtaandteam.android.wellcure;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.fabric.sdk.android.Fabric;

public class WelcomeActivity extends AppCompatActivity {

    Button EmailBTN, OTPBTN, RegisterBTN;
    final String LOG_TAG = this.getClass().getSimpleName();
    private FirebaseAuth FbAuth;
    static ProgressDialog AutoLogin;
    Button proceedButton;
    EditText EntryText;
    TextInputLayout EntryLayout;
    static ProgressDialog WelcomeProgress;
    String NotifOpenMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Fabric.with(this, new Crashlytics());
        Log.d(LOG_TAG,"Entered Welcome Activity");
        AutoLogin = new ProgressDialog(this);
        proceedButton = findViewById(R.id.ProceedButton);
        EntryText = findViewById(R.id.EntryText);
        EntryLayout = findViewById(R.id.password_confirm_layout);
        EntryLayout.setVisibility(View.INVISIBLE);
        proceedButton.setVisibility(View.INVISIBLE);
        WelcomeProgress=new ProgressDialog(this);
        Intent newIntent = getIntent();
        NotifOpenMode=newIntent.getStringExtra("Action");
        Log.d(LOG_TAG,"ExtrasBackground : "+newIntent.getStringExtra("Action"));
        if(NotifOpenMode!=null)
        {
            if(NotifOpenMode.equals("update"))
            {
                final String appPackageName = "com.gtaandteam.android.wellcure"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

                }
            }
        }



        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entry = EntryText.getText().toString().trim();
                if(TextUtils.isEmpty(entry)){
                    // is empty
                    Toast.makeText(WelcomeActivity.this,"Please Enter Email Id / Mobile Number",Toast.LENGTH_LONG).show();
                    return;
                }
                if(entry.toUpperCase().matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}"))
                {
                    Log.d(LOG_TAG,"Entry is Email");
                    checkEmailExists(entry);
                    WelcomeProgress.setMessage("Verifying Email ID");
                }
                else if(entry.length()!=10)
                {
                    Log.d(LOG_TAG,"Invalid Input");
                    Toast.makeText(WelcomeActivity.this, "Invalid Input. Please Try Again", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(entry.matches("[0-9]+"))
                {

                    WelcomeProgress.setMessage("Verifying Mobile Number");
                    entry="+91"+entry;
                    Log.d(LOG_TAG,"Possible Mobile : "+entry);
                    checkPhoneNumberExists(entry);
                }
                //TODO: DO stuff with entry
                WelcomeProgress.setCancelable(false);
                WelcomeProgress.show();
                timerDelayRemoveDialog(30000,WelcomeProgress);
            }
        });


//        EmailBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(WelcomeActivity.this, EmailLoginActivity.class));
//            }
//        });
//
//
//        OTPBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(WelcomeActivity.this, OTPLoginActivity.class));
//            }
//        });
//
//        RegisterBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
//            }
//        });
        FbAuth = FirebaseAuth.getInstance();
        Log.d(LOG_TAG,"NotifOpenMode : "+NotifOpenMode);
        Boolean openNormally=false;
        if(NotifOpenMode==null)
            openNormally=true;
        else if(!NotifOpenMode.equals("update"))
            openNormally=true;

        if(openNormally)
        {
            if(FbAuth.getCurrentUser()!=null)
            {
                AutoLogin.setMessage("Logging In Automatically");
                Log.d(LOG_TAG,"User is Not Null");
                Log.d(LOG_TAG,"User Email : "+FbAuth.getCurrentUser().getEmail());
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try
                        {
                            AutoLogin.show();

                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG,""+e.getMessage());
                        }
                    }
                }, 1000);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try
                        {
                            AutoLogin.dismiss();
                            finish();
                            Intent i =new Intent(WelcomeActivity.this, DoctorsActivity.class);
                            //i.putExtra("loginMode",1);
                            finish();
                            startActivity(i);

                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG,""+e.getMessage());
                        }
                    }
                }, 3500);

                Log.d(LOG_TAG,FbAuth.getCurrentUser().getEmail());


            }
            else
            {
                Log.d(LOG_TAG,"User is Null");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try
                        {
                            EntryLayout.setVisibility(View.VISIBLE);
                            proceedButton.setVisibility(View.VISIBLE);

                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG,""+e.getMessage());
                        }
                    }
                }, 1500);
            }

        }





    }

    @Override
    protected void onStart() {

        super.onStart();
        Log.d(LOG_TAG,"Entered onStart");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Activities Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(WelcomeActivity.this, R.style.AlertDialogCustom));
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
                Log.d(LOG_TAG, "Other Activities Exist");
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_DEL))
            Log.d(LOG_TAG,"Backspace Pressed");
        return super.onKeyDown(keyCode, event);
    }
    private void checkEmailExists(final String email)
    {
        Log.d(LOG_TAG, "Entered  checkPhoneNumberExists");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("mobileDB");
        userRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "Entered onDataChanged() " );

                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    Log.d(LOG_TAG, "Email ID Exists");
                    Intent intent = new Intent(WelcomeActivity.this, EmailLoginActivity.class);
                    intent.putExtra("Email",email);
                    intent.putExtra("Parent", LOG_TAG);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    Log.d(LOG_TAG, "Email Doesn't exist.");
                    Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                    intent.putExtra("Email",email);
                    intent.putExtra("Parent", LOG_TAG);
                    intent.putExtra("Mode", "Email");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(LOG_TAG, "Returning from checkPhoneNumberExists");
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast toast = Toast.makeText(WelcomeActivity.this, "Please Check Internet Connection\nCheck App's Internet Permissions", Toast.LENGTH_LONG);
                        TextView tv = toast.getView().findViewById(android.R.id.message);
                        if(tv!=null)
                            tv.setGravity(Gravity.CENTER);
                        toast.show();

                    }
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,""+e.getMessage());
                }
            }
        }, time);
    }
    private void checkPhoneNumberExists(final String PhoneNumber)
    {
        Log.d(LOG_TAG, "Entered  checkPhoneNumberExists");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("mobileDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "Entered onDataChanged() " );

                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    Log.d(LOG_TAG, "Phone Number Exists");
                    //OTP will be sent now
                    Log.d(LOG_TAG, "Phone Number Exists Confirmed. Hence going to start OTPopUp");
                    //(OTPLoginActivity.this, "PhoneNumberExists", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(WelcomeActivity.this, OTPopUp.class);
                    intent.putExtra("Parent", LOG_TAG);
                    intent.putExtra("PhoneNumber", PhoneNumber);
                    Log.d(LOG_TAG, "All good. Switching to OTPopUp");
                    startActivity(intent);
                }
                else
                {
                    Log.d(LOG_TAG, "Phone Number Doesn't exist.");
                    Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                    intent.putExtra("Phone",PhoneNumber);
                    intent.putExtra("Parent", LOG_TAG);
                    intent.putExtra("Mode", "Phone");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(LOG_TAG, "Returning from checkPhoneNumberExists");
    }
}
