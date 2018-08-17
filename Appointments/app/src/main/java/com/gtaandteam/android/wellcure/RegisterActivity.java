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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    /**Data Structures*/
    private String Password;
    String EmailId;
    String PhoneNumber;
    Boolean PhoneNumberExists;

    /**Views*/
    Button LoginBTN, RegisterBTN;
    EditText UsernameET, PhoneNumberET, PasswordET, ConfirmPasswordET;
    static ProgressDialog Progress;

    /**Firebase*/
    private FirebaseAuth FbAuth;


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
        Progress =new ProgressDialog(this);


        if(FbAuth.getCurrentUser()!=null)
        {
            FbAuth.signOut();
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
                hideKeyboard(RegisterActivity.this);
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
                registerUser();
            }
        });


    }
    private void registerUser()
    {
        Log.d(LOG_TAG, "Entered registerUser()");

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
        if(Password.length()<6)
        {
            Toast.makeText(this,"Minimum password length is 6",Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Minimum password length is 6, Exiting regitserUser()");
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

        Progress.setMessage("Registering Email ID ... ");
        Progress.setCancelable(false);
        Progress.show();
        timerDelayRemoveDialog(30000,Progress);
        checkPhoneNumberExists();
    }


    private void checkPhoneNumberExists()
    {
        PhoneNumberExists=false;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("mobileDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    PhoneNumberExists =true;
                    Toast.makeText(RegisterActivity.this, "Phone Number exists", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG,"Phone Number Exists");
                    Progress.dismiss();
                    return;
                }
                else
                {
                    FbAuth.createUserWithEmailAndPassword(EmailId, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //user successfully registered
                                Log.d(LOG_TAG, "Email account successfully created.");
                                Toast.makeText(RegisterActivity.this, "Email registered successfully.", Toast.LENGTH_SHORT).show();
                                //finish();
                                //startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class));

                                Log.d(LOG_TAG, "Proceeding to link Mobile Number with Email ID");
                                Toast.makeText(RegisterActivity.this, "Proceeding to link mobile number with Email ID", Toast.LENGTH_SHORT).show();
//                            Progress.setMessage("Sending OTP");
//                            Progress.show();
                                PhoneNumberExists =false;
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
                                    intent.putExtra("EmailId",EmailId);
                                    intent.putExtra("Password",Password);
                                    Log.d(LOG_TAG, "Done. Exiting setUpVerificationCallbacks() ");
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(RegisterActivity.this, "Couldn't Register.\n"+task.getException().getMessage()+" Please Try Again.", Toast.LENGTH_SHORT).show();
                                Log.d(LOG_TAG, "Couldn't create Email Account");
                                Log.d(LOG_TAG, ""+task.getException().getMessage());
                                Progress.dismiss();
                                //TODO: DISPLAY EXCEPTION MESSAGE AS TO WHY REGISTRATION COULDN'T OCCUR. I KNOW THE CODE. WILL ADD IT SOON.
                                //TODO: OK.
                            }

                        }
                    });
                    //Toast.makeText(RegisterActivity.this, "Phone number not linked to any account. Will Register. ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG,"Error : "+databaseError.getMessage());
            }
        });
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
                        Toast.makeText(RegisterActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,""+e.getMessage());
                }
            }
        }, time);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Acitivites Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(RegisterActivity.this, R.style.AlertDialogCustom));
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
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_DEL))
            Log.d(LOG_TAG,"Backspace Pressed");
        return super.onKeyDown(keyCode, event);
    }


}

