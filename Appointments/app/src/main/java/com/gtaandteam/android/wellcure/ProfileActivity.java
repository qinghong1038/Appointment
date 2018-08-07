package com.gtaandteam.android.wellcure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    Button SaveChangesBTN,
            VerifyNowBTN;

    LinearLayout EditBTN,
            VerifyMessage;

    TextView NameTV,
            EmailTV,
            PhoneTV;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: Add code to pull user name, email and phone from Firebase

        SaveChangesBTN = findViewById(R.id.SaveChanges);
        VerifyNowBTN = findViewById(R.id.VerifyNow);
        EditBTN = findViewById(R.id.EditButton);
        NameET = findViewById(R.id.NameEdit);
        EmailET = findViewById(R.id.EmailEdit);
        PhoneET = findViewById(R.id.PhoneEdit);
        NameTV = findViewById(R.id.NameTV);
        EmailTV = findViewById(R.id.EmailTV);
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



        //TODO: Add code to verify if Phone Number is verified. If not, display the message and the verify Button
        VerifyMessage.setVisibility(View.VISIBLE);
        VerifyNowBTN.setVisibility(View.VISIBLE);


        EditBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditBTN.setVisibility(View.INVISIBLE);
                SaveChangesBTN.setVisibility(View.VISIBLE);
                NameET.setVisibility(View.VISIBLE);
                EmailET.setVisibility(View.VISIBLE);
                PhoneET.setVisibility(View.VISIBLE);
                NameTV.setVisibility(View.INVISIBLE);
                EmailTV.setVisibility(View.INVISIBLE);
                PhoneTV.setVisibility(View.INVISIBLE);

                NameET.setText(NameTV.getText().toString());
                EmailET.setText(EmailTV.getText().toString());
                PhoneET.setText(PhoneTV.getText().toString());

                VerifyMessage.setVisibility(View.INVISIBLE);
                VerifyNowBTN.setVisibility(View.INVISIBLE);

                OLDPasswordET.setEnabled(true);
                NEWPasswordET.setEnabled(true);



            }
        });

        SaveChangesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveChangesBTN.setVisibility(View.INVISIBLE);
                EditBTN.setVisibility(View.VISIBLE);
                NameET.setVisibility(View.INVISIBLE);
                EmailET.setVisibility(View.INVISIBLE);
                PhoneET.setVisibility(View.INVISIBLE);
                NameTV.setVisibility(View.VISIBLE);
                EmailTV.setVisibility(View.VISIBLE);
                PhoneTV.setVisibility(View.VISIBLE);

                
                Name = NameET.getText().toString();
                Email = EmailET.getText().toString();
                Phone = PhoneET.getText().toString();
                OLDPassword = OLDPasswordET.getText().toString();
                NEWPassword = NEWPasswordET.getText().toString();

                
                NameTV.setText(Name);
                EmailTV.setText(Email);
                PhoneTV.setText(Phone);




                OLDPasswordET.setEnabled(false);
                NEWPasswordET.setEnabled(false);

                //TODO: Add code to check if old password matches. If matches, add code to change to new.
                //TODO: Add code to update the data in the Database
                //TODO: Add code to verify if Phone Number is verified. If not, display the message and the verify Button
                VerifyMessage.setVisibility(View.VISIBLE);
                VerifyNowBTN.setVisibility(View.VISIBLE);

            }
        });


        VerifyNowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //TODO: Add code to call OTPPopUp

                
            }
        });


    }
}
