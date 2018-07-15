package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {

    /**Data Structures*/
    String BookingDate, OrderID, TokenID, PaymentID, AppointmentDate;
    Boolean Success;

    /**Views*/
    Button GoBackBTN;
    TextView StatusHeaderTV, StatusMessageTV, BookingIDTV, TimeOfBookingTV, TimeOfAppointmentTV;
    ImageView StatusIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Linking to views
        GoBackBTN = findViewById(R.id.BackBTN);
        StatusIV = findViewById(R.id.StatusIV);
        StatusHeaderTV = findViewById(R.id.StatusTV);
        StatusMessageTV = findViewById(R.id.StatusMessageTV);
        BookingIDTV = findViewById(R.id.BookingIDET);
        StatusIV = findViewById(R.id.StatusIV);
        TimeOfBookingTV = findViewById(R.id.BookingDateET);
        TimeOfAppointmentTV = findViewById(R.id.AppointmentDateET);

        //Side note : prefer to use "hh:mm,  dd-mm-yyyy" for displaying time of completion.
        Intent getStatus = getIntent();
        Success = getStatus.getBooleanExtra("Status",false);
        BookingDate =AppointmentActivity.TodaysDate;
        AppointmentDate =AppointmentActivity.SelectedDate;

        if(Success)
        {
            StatusIV.setImageResource(R.drawable.success);
            OrderID =getStatus.getStringExtra("OrderID");
            PaymentID =getStatus.getStringExtra("PaymentID");
            TokenID =getStatus.getStringExtra("PaymentToken");
            BookingIDTV.setText(PaymentID);
            TimeOfBookingTV.setText(BookingDate);
            TimeOfAppointmentTV.setText(AppointmentDate);
            StatusMessageTV.setText("You have Successfully Made a Booking!");
            StatusHeaderTV.setText("SUCCESS!");
        }
        else
        {
            StatusIV.setImageResource(R.drawable.error);
            StatusHeaderTV.setText("ERROR!");
            StatusMessageTV.setText("Payment Failed! Please Try Again!");
            BookingIDTV.setVisibility(View.INVISIBLE);
            TimeOfBookingTV.setVisibility(View.INVISIBLE);
        }

        //TextView StatusHeader, StatusMessage, BookingID, TimeOfBooking;
        GoBackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusActivity.this, DoctorsActivity.class));
                finish();
            }
        });
    }
}
