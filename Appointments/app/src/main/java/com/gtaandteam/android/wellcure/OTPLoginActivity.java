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
import android.text.Selection;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OTPLoginActivity extends AppCompatActivity {

    /**Data Structures*/
    String UserId; //Stores the user input as a String.
    String PhoneNumber;
    Boolean PhoneNumberExists;

    /**Views*/
    TextView SwitchToEmailTV;
    EditText PhoneOTPTV;
    private ProgressDialog Progress;
    Button SendOTPBTN;//Send OTP Button
    Button RegisterBTN;
    /**Firebase*/

    Toolbar MyToolbar;


    final String LOG_TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        //Linking to views
        SwitchToEmailTV = findViewById(R.id.SwitchToEmailTV);
        SendOTPBTN = findViewById(R.id.SendOTPBTN);
        PhoneOTPTV = findViewById(R.id.PhoneNumberET);
        RegisterBTN = findViewById(R.id.RegisterBTN);

        RegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OTPLoginActivity.this, RegisterActivity.class));
            }
        });
        Progress =new ProgressDialog(this);


        MyToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(MyToolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        PhoneOTPTV.setText("+91 ");
        Selection.setSelection(PhoneOTPTV.getText(), PhoneOTPTV.getText().length());

        SendOTPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneNumber = PhoneOTPTV.getText().toString().trim().replaceAll(" ", "" );
                hideKeyboard(OTPLoginActivity.this);
                if(PhoneNumber.length()==13)
                {
                    if(PhoneNumber.startsWith("+91"))
                    {
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
                        Progress.setMessage("Validating Mobile Number");
                        Progress.show();

                        checkPhoneNumberExists();



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

    private void checkPhoneNumberExists()
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
                    Toast.makeText(OTPLoginActivity.this, "PhoneNumberExists", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(OTPLoginActivity.this, OTPopUp.class);
                    intent.putExtra("Parent", LOG_TAG);
                    intent.putExtra("PhoneNumber", PhoneNumber);
                    Log.d(LOG_TAG, "All good. Switching to OTPopUp");
                    Progress.dismiss();
                    startActivity(intent);
                    Log.d(LOG_TAG, "PhoneNumberExists is set to: " + PhoneNumberExists);
                   // bool = true;
                    return;
                }
                else
                {
                    Log.d(LOG_TAG, "Phone Number Doesn't exist.");
                    Toast.makeText(OTPLoginActivity.this, "Phone number not linked to any account.\nPlease Register or Login Using Email. ", Toast.LENGTH_SHORT).show();
                    Progress.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(LOG_TAG, "Returning from checkPhoneNumberExists");
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
        Log.d("OTPLoginActivity","Keyboard Closed");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");
        }
        if(isTaskRoot())
        {
            Log.d(LOG_TAG,"No other Acitivites Exist");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(OTPLoginActivity.this, R.style.AlertDialogCustom));
            builder.setCancelable(true);
            builder.setTitle("Exit App");
            builder.setMessage("Are you sure you want to Exit?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"Exiting App");
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
        else
        {
            Log.d(LOG_TAG,"Other Acitivites Exist");
        }
        return super.onKeyDown(keyCode, event);
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(OTPLoginActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
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
