package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import io.fabric.sdk.android.Fabric;

public class WelcomeActivity extends AppCompatActivity {

    Button EmailBTN, OTPBTN, RegisterBTN;
    final String LOG_TAG = this.getClass().getSimpleName();
    private FirebaseAuth FbAuth;
    static ProgressDialog AutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Fabric.with(this, new Crashlytics());
        EmailBTN = findViewById(R.id.EmailButton);
        OTPBTN = findViewById(R.id.OTPButton);
        RegisterBTN = findViewById(R.id.RegisterButton);
        AutoLogin = new ProgressDialog(this);

        EmailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, EmailLoginActivity.class));
            }
        });


        OTPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, OTPLoginActivity.class));
            }
        });

        RegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
            }
        });
        FbAuth = FirebaseAuth.getInstance();
        if(FbAuth.getCurrentUser()!=null)
        {
            EmailBTN.setVisibility(View.INVISIBLE);
            OTPBTN.setVisibility(View.INVISIBLE);
            RegisterBTN.setVisibility(View.INVISIBLE);
            AutoLogin.setMessage("Logging In Automatically");

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
            }, 3000);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try
                    {
                        AutoLogin.dismiss();
                        finish();
                        Intent i =new Intent(WelcomeActivity.this, DoctorsActivity.class);
                        //i.putExtra("loginMode",1);
                        startActivity(i);

                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG,""+e.getMessage());
                    }
                }
            }, 5000);

            Log.d(LOG_TAG,FbAuth.getCurrentUser().getEmail());

            //FbAuth.signOut();
        }





    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Acitivites Exist");
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
}
