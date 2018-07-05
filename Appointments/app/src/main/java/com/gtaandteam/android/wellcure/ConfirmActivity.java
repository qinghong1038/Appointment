package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmActivity extends AppCompatActivity {


    Button ConfirmButton;
    TextView DoctorName, Date, PatientName, Email, Amount;
    ImageView DoctorPhoto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        ConfirmButton = findViewById(R.id.PayButton);
        DoctorName = findViewById(R.id.DoctorName);
        Date = findViewById(R.id.DateValue); //Date of appointment
        PatientName = findViewById(R.id.NameValue);
        Email = findViewById(R.id.EmailValue);
        Amount = findViewById(R.id.Amount); //Amount to be paid
        DoctorPhoto = findViewById(R.id.profile_photo);

        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Change this to redirect to Paytm
                startActivity(new Intent(ConfirmActivity.this, StatusActivity.class));
                finish();

            }
        });


    }
    /*public void retrieve() {

        userDb2 = FirebaseDatabase.getInstance().getReference("users").child(fbAuth.getCurrentUser().getUid());
        userDb2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                progress.setMessage("Loading Details of User");
                progress.show();
                String ss = dataSnapshot.getKey().toString();
                if (ss.equals("Name")) {
                    rName= dataSnapshot.getValue().toString();
                } else if (ss.equals("Email"))
                    rEmail= dataSnapshot.getValue().toString();
                else if (ss.equals("Phone"))
                    rPhone= dataSnapshot.getValue().toString();
                else if (ss.equals("Date"))
                    rDate= dataSnapshot.getValue().toString();
                Name.setText(rName);
                Email.setText(rEmail);
                Phone.setText(rPhone);
                progress.dismiss();
                //ok

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }*/
}
