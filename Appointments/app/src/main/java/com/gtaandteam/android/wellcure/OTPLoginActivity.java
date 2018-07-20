package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OTPLoginActivity extends AppCompatActivity {

    /**Data Structures*/
    String UserId; //Stores the user input as a String.
    String PhoneNumber;
    private String PhoneVerificationId;
    Boolean PhoneNumberExists;

    /**Views*/
    TextView SwitchToEmailTV;
    EditText PhoneOTPTV;
    private ProgressDialog Progress;
    Button SendOTPBTN;//Send OTP Button
    Button RegisterBTN;
    /**Firebase*/
    private FirebaseAuth FbAuth;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;

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
        FbAuth = FirebaseAuth.getInstance();

        PhoneOTPTV.setText("+91 ");
        Selection.setSelection(PhoneOTPTV.getText(), PhoneOTPTV.getText().length());

        SendOTPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNumber = PhoneOTPTV.getText().toString().trim().replaceAll(" ", "" );

                if(PhoneNumber.length()==13)
                {
                    if(PhoneNumber.startsWith("+91"))
                    {

                        //TODO: Need to check if not in Database
                        checkPhoneNumberExists();
                        if(PhoneNumberExists) {
                            //OTP will be sent now
                            Progress.setMessage("Sending OTP");
                            Progress.show();
                            Intent intent = new Intent(OTPLoginActivity.this, OTPopUp.class);
                            intent.putExtra("Parent", LOG_TAG);
                            intent.putExtra("PhoneNumber", PhoneNumber);
                            Log.d(LOG_TAG, "All good. Switching to OTPopUp");
                            startActivity(intent);
                        }



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
        PhoneNumberExists=false;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDB");
        userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    PhoneNumberExists =true;
                    Toast.makeText(OTPLoginActivity.this, "Phone Number exists", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Toast.makeText(OTPLoginActivity.this, "Phone number not linked to any account. Please Register. ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
