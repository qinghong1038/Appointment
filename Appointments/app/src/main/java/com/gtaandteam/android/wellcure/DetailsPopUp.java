package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsPopUp extends Activity {

    TextView Doctor, Name, Date, Fees;
    ImageView DoctorImage;

    //Sorry for inconsistent naming across activities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_pop_up);

        //String CurrentName = getIntent().getStringExtra("Name");
        String CurrentDoctor = getIntent().getStringExtra("Doctor");
        String CurrentDate = getIntent().getStringExtra("Date");
        int CurrentDoctorImage = getIntent().getIntExtra("DoctorImage", -1);
        // float CurrentFees = getIntent().getFloatExtra("Fees", 0);

        Doctor = findViewById(R.id.DoctorName);
        Name = findViewById(R.id.NameValue);
        Date = findViewById(R.id.DateValue);
        Fees = findViewById(R.id.Amount);
        DoctorImage = findViewById(R.id.DoctorImage);

        Doctor.setText(CurrentDoctor);
        Date.setText(CurrentDate);
        DoctorImage.setImageResource(CurrentDoctorImage);

        //Name.setText(CurrentName);
        //Fees.setText(CurrentFees);
    }
}
