package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.fabric.sdk.android.Fabric;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

public class EmailLoginActivity extends AppCompatActivity {
    //TODO: What else?
    /**Views*/
    EditText UsernameET, PasswordET;
    Button RegisterBTN, LoginBTN;
    TextView RegisterTV, ForgotPasswordTV;
    private ProgressDialog Progress;
    Toolbar MyToolbar;

    /**Firebase*/
    private FirebaseAuth FbAuth;
    final String LOG_TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        //Linking to views
        ForgotPasswordTV = findViewById(R.id.ForgotPasswordTV);
        LoginBTN = findViewById(R.id.LoginBTN);
        UsernameET =findViewById(R.id.UsernameET);
        PasswordET =findViewById(R.id.PasswordET);
        RegisterTV = findViewById(R.id.NoAccountTV);
        RegisterBTN =findViewById(R.id.RegisterBTN);
        final TextView OTPLogin =  findViewById(R.id.SwitchToOTPTV);

        FbAuth = FirebaseAuth.getInstance();
        Progress =new ProgressDialog(this);

        MyToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(MyToolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        String Parent=intent.getStringExtra("Parent");
        if(Parent==null)
            Parent="";
        if(Parent.equals("OTPopUp"))
            if(OTPopUp.Progress.isShowing()){
            Log.d(LOG_TAG,"OTP Progress was showing");
            OTPopUp.Progress.dismiss();
            }

        OTPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OTPLoginActivity.class));
                finish();

            }
        });

        if(FbAuth.getCurrentUser()!=null)
        {
            //User already logged in, previous login credentials stored in PhoneNumber
            //then skip login and directly go to choosing doctor page
            /*finish();
            Log.d(LOG_TAG,FbAuth.getCurrentUser().getEmail());
            Intent i =new Intent(EmailLoginActivity.this, DoctorsActivity.class);
            i.putExtra("loginMode",1);
            startActivity(i);*/
            FbAuth.signOut();
        }
        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(EmailLoginActivity.this);
                if(FbAuth.getCurrentUser()!=null)
                {
                    //User already logged in, previous login credentials stored in PhoneNumber
                    //then skip login and directly go to choosing doctor page
                    //finish();
                    //Intent i =new Intent(EmailLoginActivity.this, DoctorsActivity.class);
                    //i.putExtra("loginMode",1);
                    //startActivity(i);
                    FbAuth.signOut();
                }
                if(!isConnected()) {
                    Snackbar sb = Snackbar.make(view, "No Internet Connectivity", Snackbar.LENGTH_LONG);
                    sb.getView().setBackgroundColor(getResources().getColor(R.color.darkred));
                    sb.show();
                    Log.d(LOG_TAG,"No Internet");
                    return;
                }
                else
                {
                    Log.d(LOG_TAG,"Internet is connected");
                }
                userLogin(view);

            }
        });


        RegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(EmailLoginActivity.this,RegisterActivity.class));
            }
        });
        RegisterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(EmailLoginActivity.this,RegisterActivity.class));
            }
        });
        ForgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = UsernameET.getText().toString().trim();
                if(emailAddress.equals(""))
                {
                    Toast.makeText(EmailLoginActivity.this, "Please Enter The Email ID and Try Again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(LOG_TAG, "Email sent.");
                                        Toast.makeText(EmailLoginActivity.this, "Email with Reset Link Sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

    }
    private void userLogin(View view){

        String email= UsernameET.getText().toString().trim();
        String pass= PasswordET.getText().toString().trim();
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



        Progress.setMessage("Logging In");
        Progress.setCancelable(false);
        Progress.show();
        timerDelayRemoveDialog(20000,Progress);
        FbAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Progress.dismiss();
                        if(task.isSuccessful())
                        {
                            //user successfully logged in
                            //we start doctor activity here
                            Toast.makeText(EmailLoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i =new Intent(EmailLoginActivity.this, DoctorsActivity.class);
                            i.putExtra("loginMode",1);
                            startActivity(i);
                        }
                        else
                        {
                            if(task.getException().toString().contains("com.google.firebase.FirebaseNetworkException"))
                            {
                                Toast.makeText(EmailLoginActivity.this, "Internet Connectivity Issues", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(EmailLoginActivity.this,"Couldn't Login. Please Try Again.." +
                                        "\n"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                            Log.d(LOG_TAG,"Error : "+task.getException());

                        }
                    }
                });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Acitivites Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(EmailLoginActivity.this, R.style.AlertDialogCustom));
                builder.setCancelable(true);
                builder.setTitle("Exit App  ");
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
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_DEL))
            Log.d(LOG_TAG,"Backspace Pressed");
        return super.onKeyDown(keyCode, event);
    }
    public boolean isConnected()
    {
        String command = "ping -c 1 google.com";
        Boolean isConnectedVar=false;
        try{

            isConnectedVar = (Runtime.getRuntime().exec (command).waitFor() == 0);
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG,"Exception : "+e.getMessage());
        }
        return isConnectedVar;
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
        Log.d("EmailLoginActivity","Keyboard Closed");
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(EmailLoginActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
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
