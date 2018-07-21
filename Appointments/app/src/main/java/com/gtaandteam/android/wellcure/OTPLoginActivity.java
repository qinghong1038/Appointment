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
                        Progress.setMessage("Validating Mobile Number");
                        Progress.show();
                        //TODO: Need to check if not in Database
                        //Commented for Testing
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
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDB");
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
                    Toast.makeText(OTPLoginActivity.this, "Phone number not linked to any account. Please Register. ", Toast.LENGTH_SHORT).show();
                    Progress.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(LOG_TAG, "Returning from checkPhoneNumberExists");
    }

}
