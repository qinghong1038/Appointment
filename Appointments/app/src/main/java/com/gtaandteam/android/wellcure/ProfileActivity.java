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
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    Button SaveChangesBTN,
            VerifyNowBTN;

    LinearLayout EditBTN,
            VerifyMessage;

    TextView NameTV,
            EmailTV,
            PhoneTV,
            EditButtonText;

    EditText NameET,
            EmailET,
            PhoneET,
            OLDPasswordET,
            NEWPasswordET;
    Toolbar MyToolbar;

    String Name,
            Email,
            Phone,
            OLDPassword,
            NEWPassword;

    Boolean EditMode;
    Boolean PhoneNumberExists,LinkingStatus;
    String Parent;

    ImageView EditButtonImg;
    ProgressDialog Progress;
    static ProgressDialog Progress2;

    //Firebase
    FirebaseAuth FbAuth;
    FirebaseUser user;
    List<UserInfo> profileData;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        LinkingStatus = intent.getBooleanExtra("LinkingStatus",false);
        Parent=intent.getStringExtra("Parent");
        //TODO: Add code to pull user name, email and phone from Firebase

        Progress2 = new ProgressDialog(this);
        EditMode=true;
        FbAuth=FirebaseAuth.getInstance();
        user=FbAuth.getCurrentUser();
        Name=user.getDisplayName();
        Email=user.getEmail();
        Phone=user.getPhoneNumber();
        Log.d(LOG_TAG,"Email ID : "+Email);
        Log.d(LOG_TAG,"Phone : "+Phone);
        Log.d(LOG_TAG,"Name : "+Name);

        SaveChangesBTN = findViewById(R.id.SaveChanges);
        VerifyNowBTN = findViewById(R.id.VerifyNow);
        EditBTN = findViewById(R.id.EditButton);
        EditButtonText=findViewById(R.id.EditButtonText);
        EditButtonImg=findViewById(R.id.EditButtonImg);
        NameET = findViewById(R.id.NameEdit);
        EmailET = findViewById(R.id.EmailEdit);
        PhoneET = findViewById(R.id.PhoneEdit);
        NameTV = findViewById(R.id.NameTV);
        EmailTV = findViewById(R.id.FeesTV);
        PhoneTV = findViewById(R.id.PhoneTV);
        VerifyMessage = findViewById(R.id.NotVerifiedMessage);
        OLDPasswordET = findViewById(R.id.OLDPasswordET);
        NEWPasswordET = findViewById(R.id.NEWPasswordET);
        MyToolbar = findViewById(R.id.MyToolbar);


        OLDPasswordET.setEnabled(false);
        NEWPasswordET.setEnabled(false);


        NameET.setVisibility(View.INVISIBLE);
        EmailET.setVisibility(View.INVISIBLE);
        PhoneET.setVisibility(View.INVISIBLE);
        VerifyMessage.setVisibility(View.INVISIBLE);
        SaveChangesBTN.setVisibility(View.INVISIBLE);
        VerifyNowBTN.setVisibility(View.INVISIBLE);
        setSupportActionBar(MyToolbar);

        Progress =new ProgressDialog(this);

        EmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!EditMode)
                    Toast.makeText(ProfileActivity.this, "Not Allowed To Change Email ID", Toast.LENGTH_SHORT).show();
            }
        });
        //TODO: Add code to verify if Phone Number is verified. If not, display the message and the verify Button
        if(Phone==null)
        {
            VerifyMessage.setVisibility(View.VISIBLE);
            VerifyNowBTN.setVisibility(View.VISIBLE);
        }


        if(Name==null)
            NameTV.setText("Not Set");
        else
            NameTV.setText(Name);
        if(Phone==null) {
            PhoneET.setText("+91 ");
            PhoneTV.setText("+91 ");
        }
        else
            PhoneTV.setText(Phone);
        EmailTV.setText(Email);


        /*try{
            if(Name.equals("")||Name==null)
            {
                Name="";
                Log.d(LOG_TAG,"Name was null. No exception");
            }
            else
            {
                NameTV.setText(Name);
            }

        }
        catch (Exception e)
        {
            Name="";
            Log.d(LOG_TAG,"Inside Exception. Error : "+e.getMessage());
        }
        try{
            if(Name.equals("")||Name==null)
            {
                Name="";
                Log.d(LOG_TAG,"Name was null. No exception");
            }
            else
            {
                NameTV.setText(Name);
            }

        }
        catch (Exception e)
        {
            Name="";
            Log.d(LOG_TAG,"Inside Exception. Error : "+e.getMessage());
        }
        */

        //PhoneTV.setText(Phone);

        EditBTN.setOnClickListener(new View.OnClickListener() {
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
                Log.d(LOG_TAG,"Entered EditButton");
                Log.d(LOG_TAG,"Email ID : "+Email);
                Log.d(LOG_TAG,"Phone : "+Phone);
                Log.d(LOG_TAG,"Name : "+Name);
                if(EditMode)
                {
                    EditButtonImg.setVisibility(View.INVISIBLE);
                    EditButtonText.setText("Cancel");
                    SaveChangesBTN.setVisibility(View.VISIBLE);
                    NameET.setVisibility(View.VISIBLE);
                    EmailET.setVisibility(View.INVISIBLE);
                    PhoneET.setVisibility(View.VISIBLE);
                    if(user.getPhoneNumber()!=null)
                        PhoneET.setFocusable(false);
                    NameTV.setVisibility(View.INVISIBLE);
                    EmailTV.setVisibility(View.VISIBLE);
                    PhoneTV.setVisibility(View.INVISIBLE);

                    NameET.setText(NameTV.getText().toString());
                    EmailET.setText(EmailTV.getText().toString());
                    PhoneET.setText(PhoneTV.getText().toString());

                    VerifyMessage.setVisibility(View.INVISIBLE);
                    VerifyNowBTN.setVisibility(View.INVISIBLE);

                    OLDPasswordET.setEnabled(true);
                    NEWPasswordET.setEnabled(true);
                    EditMode=false;
                }
                else
                {
                    EditMode=true;
                    SaveChangesBTN.setVisibility(View.INVISIBLE);
                    EditButtonImg.setVisibility(View.VISIBLE);
                    EditButtonText.setText("Edit");
                    NameET.setVisibility(View.INVISIBLE);
                    EmailET.setVisibility(View.INVISIBLE);
                    PhoneET.setVisibility(View.INVISIBLE);
                    NameTV.setVisibility(View.VISIBLE);
                    EmailTV.setVisibility(View.VISIBLE);
                    PhoneTV.setVisibility(View.VISIBLE);

                    OLDPasswordET.setText("");
                    NEWPasswordET.setText("");

                    Name=user.getDisplayName();
                    if(Name==null) {
                        NameTV.setText("Not Set");
                        NameET.setText("Not Set");
                    }
                    else
                    {
                        NameTV.setText(Name);
                        NameET.setText(Name);
                    }
                    Phone=user.getPhoneNumber();
                    if(Phone==null) {
                        PhoneTV.setText("+91 ");
                        PhoneET.setText("+91 ");
                    }
                    else {
                        PhoneTV.setText(Phone);
                        PhoneET.setText(Phone);
                    }

                    EmailTV.setText(Email);




                    OLDPasswordET.setEnabled(false);
                    NEWPasswordET.setEnabled(false);


                    if(user.getPhoneNumber()==null) {
                        VerifyMessage.setVisibility(View.VISIBLE);
                        VerifyNowBTN.setVisibility(View.VISIBLE);
                    }

                }




            }
        });

        SaveChangesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(ProfileActivity.this);
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
                EditMode=true;
                SaveChangesBTN.setVisibility(View.INVISIBLE);
                EditButtonImg.setVisibility(View.VISIBLE);
                EditButtonText.setText("Edit");
                SaveChangesBTN.setVisibility(View.INVISIBLE);
                EditBTN.setVisibility(View.VISIBLE);
                NameET.setVisibility(View.INVISIBLE);
                EmailET.setVisibility(View.INVISIBLE);
                PhoneET.setVisibility(View.INVISIBLE);
                NameTV.setVisibility(View.VISIBLE);
                EmailTV.setVisibility(View.VISIBLE);
                PhoneTV.setVisibility(View.VISIBLE);
                OLDPasswordET.setEnabled(false);
                NEWPasswordET.setEnabled(false);

                Name = NameET.getText().toString();
                Email = EmailET.getText().toString();
                Phone = PhoneET.getText().toString().trim().replaceAll(" ", "" );
                OLDPassword = OLDPasswordET.getText().toString();
                NEWPassword = NEWPasswordET.getText().toString();

                String NameInFB=user.getDisplayName();
                NameTV.setText(Name);
                EmailTV.setText(Email);
                PhoneTV.setText(Phone);
                Log.d(LOG_TAG,"Entered SaveChanges");
                Log.d(LOG_TAG,"Email ID : "+Email);
                Log.d(LOG_TAG,"Phone : "+Phone);
                Log.d(LOG_TAG,"Name : "+Name);
                if(NameInFB!=null)
                {
                    if(!TextUtils.equals(Name, NameInFB)){
                        Toast.makeText(ProfileActivity.this,"Names different in DB and EditText",Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Names different in DB and EditText");
                        if(Name.trim().equals(""))
                        {
                            Toast.makeText(ProfileActivity.this,"Please Enter Valid Name",Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Not Valid Name");
                            return;
                        }
                        updateDisplayName(Name);
                    }
                }
                else {
                    if(Name.trim().equals(""))
                    {
                        Toast.makeText(ProfileActivity.this,"Please Enter Valid Name",Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Not Valid Name");
                        return;
                    }
                    updateDisplayName(Name);
                }
                if((!TextUtils.isEmpty(OLDPassword))||(!TextUtils.isEmpty(NEWPassword))){
                    // is empty
                    Toast.makeText(ProfileActivity.this,"Changing Password",Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Changing Password");
                    if(!TextUtils.equals(OLDPassword, NEWPassword)){
                        // is empty
                        Toast.makeText(ProfileActivity.this,"Passwords do not match.",Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Passwords do not match");
                        return;
                    }
                    if(OLDPassword.length()<6)
                    {
                        Toast.makeText(ProfileActivity.this,"Minimum password length is 6",Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Minimum password length is 6");
                        return;
                    }
                    user.updatePassword(OLDPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(LOG_TAG, "User password updated.");
                                        Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


                OLDPasswordET.setEnabled(false);
                NEWPasswordET.setEnabled(false);


                //TODO: Add code to update the data in the Database
                if(user.getPhoneNumber()==null)
                {
                    VerifyMessage.setVisibility(View.VISIBLE);
                    VerifyNowBTN.setVisibility(View.VISIBLE);
                }


            }
        });


        VerifyNowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(ProfileActivity.this);
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
                if(user.getPhoneNumber()!=null)
                    return;
                String PhoneNumber=PhoneET.getText().toString().trim().replaceAll(" ", "" );
                if(PhoneNumber.length()==13)
                {
                    if(PhoneNumber.startsWith("+91"))
                    {
                        Log.d(LOG_TAG, "Phone Number is good.");
                    }
                    else
                    {
                        Progress2.dismiss();
                        Toast.makeText(ProfileActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
                        PhoneET.setText("+91 ");
                        Selection.setSelection(PhoneET.getText(), PhoneET.getText().length());
                        Log.d(LOG_TAG, "Invalid Phone Number");
                        return;

                    }
                }
                else
                {
                    Progress2.dismiss();
                    Toast.makeText(ProfileActivity.this, "Please enter a valid phone number after the +91", Toast.LENGTH_LONG).show();
                    PhoneET.setText("+91 ");
                    Selection.setSelection(PhoneET.getText(), PhoneET.getText().length());
                    Log.d(LOG_TAG, "Invalid Phone Number, Exiting regitserUser()");
                    return;

                }
                Progress2.setMessage("Proceeding to Verify Mobile Number");
                Progress2.show();

                checkPhoneNumberExists(PhoneNumber);


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
                        new ContextThemeWrapper(ProfileActivity.this, R.style.AlertDialogCustom));
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
    private void checkPhoneNumberExists(String Phone)
    {

        final String PhoneNumber=Phone;
        PhoneNumberExists=false;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("mobileDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    PhoneNumberExists =true;
                    Toast.makeText(ProfileActivity.this, "Phone Number already exists", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG,"Phone Number Exists");
                    PhoneET.setText("+91 ");
                    PhoneTV.setText("+91 ");
                    Progress2.dismiss();
                    return;
                }
                else
                {
                    Log.d(LOG_TAG, "Proceeding to link Mobile Number with Email ID");
                    Toast.makeText(ProfileActivity.this, "Proceeding to link mobile number with Email ID", Toast.LENGTH_SHORT).show();
//                            Progress.setMessage("Sending OTP");
//                            Progress.show();
                    Log.d(LOG_TAG, "All good, Calling OTPopUp Activity");
                    Intent intent = new Intent(ProfileActivity.this, OTPopUp.class);
                    intent.putExtra("PhoneNumber", PhoneNumber);
                    intent.putExtra("Parent", LOG_TAG);
                    startActivity(intent);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void updateDisplayName(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User profile updated.");
                        }
                    }
                });
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(ProfileActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,""+e.getMessage());
                }
            }
        }, time);
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
}
