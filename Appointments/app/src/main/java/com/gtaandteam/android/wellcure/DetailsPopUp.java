package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsPopUp extends Activity {

    /**Views*/
    TextView DoctorTV, NameTV, DateTV, FeesTV;
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

        //String CurrentName = getIntent().getStringExtra("NameET");
        String CurrentDoctor = getIntent().getStringExtra("Doctor");
        String CurrentDate = getIntent().getStringExtra("DateTV");
        int CurrentDoctorImage = getIntent().getIntExtra("DoctorImage", -1);
        float CurrentFees = getIntent().getFloatExtra("Fees", 0);



        DoctorTV.setText(CurrentDoctor);
        DateTV.setText(CurrentDate);
        DoctorIV.setImageResource(CurrentDoctorImage);

        //NameET.setText(CurrentName);
        FeesTV.setText("" +CurrentFees);
    }
}
