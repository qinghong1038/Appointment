package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsPopUp extends Activity {

    /**Views*/
    TextView DoctorTV, NameTV, DateTV, FeesTV,BookedOnTV,EmailTV,MobileTV;
    ImageView DoctorIV;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_pop_up);

        //Linking to Views
        DoctorTV = findViewById(R.id.DoctorNameTV);
        NameTV = findViewById(R.id.NameET);
        DateTV = findViewById(R.id.DateET);
        FeesTV = findViewById(R.id.AmountTV);
        DoctorIV = findViewById(R.id.DoctorIV);
        BookedOnTV = findViewById(R.id.BookedOnET);
        EmailTV=findViewById(R.id.EmailET);
        MobileTV=findViewById(R.id.MobileET);

        String CurrentName = getIntent().getStringExtra("PatientName");
        String CurrentDoctor = getIntent().getStringExtra("Doctor");
        String CurrentDate = getIntent().getStringExtra("DateTV");
        int CurrentDoctorImage = getIntent().getIntExtra("DoctorImage", -1);
        float CurrentFees = getIntent().getFloatExtra("Fees", 0);
        String CurrentBookedOn = getIntent().getStringExtra("BookedOn");
        String CurrentEmailID=getIntent().getStringExtra("EmailID");
        String CurrentMobile=getIntent().getStringExtra("MobileNumber");

        
        
        

        int IntCurrentFees=(int)CurrentFees;



        DoctorTV.setText(CurrentDoctor);
        DateTV.setText(CurrentDate);
        DoctorIV.setImageResource(CurrentDoctorImage);
        BookedOnTV.setText(CurrentBookedOn);
        FeesTV.setText("₹"+IntCurrentFees);
        EmailTV.setText(CurrentEmailID);
        MobileTV.setText(CurrentMobile);
        NameTV.setText(CurrentName);
        //FeesTV.setText("" +CurrentFees);
    }
}
