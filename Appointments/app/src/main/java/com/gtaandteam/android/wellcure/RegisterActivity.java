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
    private ProgressDialog Progress;

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
//                            Progress.setMessage("Sending OTP");
//                            Progress.show();
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
                                intent.putExtra("EmailId",EmailId);
                                intent.putExtra("Password",Password);
                                Log.d(LOG_TAG, "Done. Exiting setUpVerificationCallbacks() ");
                                startActivity(intent);
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Couldn't register. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Couldn't create Email Account");
                            //TODO: DISPLAY EXCEPTION MESSAGE AS TO WHY REGISTRATION COULDN'T OCCUR. I KNOW THE CODE. WILL ADD IT SOON.
                            //TODO: OK.
                        }
                    }
                });


    }


    private void checkPhoneNumberExists()
    {
        PhoneNumberExists=false;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    PhoneNumberExists =true;
                    Toast.makeText(RegisterActivity.this, "Phone Number exists", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Toast.makeText(RegisterActivity.this, "Phone number not linked to any account. Please Register. ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

