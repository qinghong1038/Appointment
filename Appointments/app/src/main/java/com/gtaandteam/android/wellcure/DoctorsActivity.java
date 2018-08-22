package com.gtaandteam.android.wellcure;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class DoctorsActivity extends AppCompatActivity {

    /*
    TODO: Make a list to display multiple doctors
    todo: and then choose one to go to that doctor's page
     */

    /**Data Structures*/
    Boolean UserExists;
    static String DoctorName = "Ritu Jain";

    /**Views*/
    Button AppointmentBTN;
    Toolbar MyToolbar;

    /**Firebase*/
    DatabaseReference UserDb1;
    FirebaseUser FbUser;
    private FirebaseAuth FbAuth;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_doctors);

        //Linking to views
        MyToolbar = findViewById(R.id.MyToolbar);
        AppointmentBTN = findViewById(R.id.get_appointment);


        FbAuth = FirebaseAuth.getInstance();
        FbUser = FbAuth.getCurrentUser();
        setSupportActionBar(MyToolbar);

        /*if(FbUser.getPhoneNumber() == null){
            signOut();
            startActivity(new Intent(DoctorsActivity.this, EmailLoginActivity.class));
            finish();


        }*/


        //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "" + FbUser.getPhoneNumber());


        Intent intent = getIntent();	//gives the reference to the destination intent
        final int loginMode = intent .getIntExtra("loginMode",0);	//loginMode is given in EmailLoginActivity and OTPLoginAcitivty


        AppointmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("UserExists", UserExists);
                startActivity(intent);



            }
        });

        storeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_SignOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(DoctorsActivity.this, R.style.AlertDialogCustom));
                builder.setCancelable(true);
                builder.setTitle("Sign Out");
                builder.setMessage("Are you sure you want to sign out?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                                finish();
                                //Toast.makeText(getApplicationContext(), "Signing Out", Toast.LENGTH_LONG).show();

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

                return true;
            case R.id.action_past:
                Intent intent = new Intent(DoctorsActivity.this, PastActivity.class);
                intent.putExtra("Parent", LOG_TAG);
                startActivity(intent);
                return true;
            case R.id.action_About:
                startActivity(new Intent(DoctorsActivity.this, AboutActivity.class));
                return true;
            case R.id.action_profile:
                startActivity(new Intent(DoctorsActivity.this, ProfileActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }
    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }

    public void signOut() {
        FbAuth.signOut();
    }

    public void storeData(){
        /**Stores user data in the database*/

        Log.d(LOG_TAG,"Entered storeData() Function");
        //FirstName = NameET.getText().toString().trim();
        //second_name=etSecondName.getText().toString();
        String rName=FbAuth.getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(rName)) {
            rName="";
        }
        Date startDate = Calendar.getInstance().getTime();
        //String rName=FbAuth.getCurrentUser().getDisplayName();
        HashMap<String,String> Data= new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String Date = format.format(startDate);
        Log.d(LOG_TAG,""+Date);
        String PhoneNumber;
        try {
            PhoneNumber = FbAuth.getCurrentUser().getPhoneNumber().substring(3);
        }
        catch (Exception e)
        {
            PhoneNumber="Not Verified";
            Log.d(LOG_TAG,"Error : "+e.getMessage());
        }
        String Email=FbAuth.getCurrentUser().getEmail();
        //Date = Day +"/"+(Month +1)+"/"+ Year;
        //Data = new HashMap<>();
        Data.put("Name", rName);
        Data.put("Email", Email);
        Data.put("Phone", PhoneNumber);
        Data.put("LoginDate", Date);
        Log.d(LOG_TAG,"Hashmap Done");
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("users");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //finish();
                //go to page which shows users details
                Log.d(LOG_TAG,"Stored to Database");

            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");


                Log.d(LOG_TAG, "No other Activities Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(DoctorsActivity.this, R.style.AlertDialogCustom));
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
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(DoctorsActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_LONG).show();
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