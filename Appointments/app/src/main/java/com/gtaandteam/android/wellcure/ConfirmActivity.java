package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
}
