package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {

    Button GoBackbutton;
    TextView StatusHeader, StatusMessage, BookingID, TimeOfBooking;
    ImageView StatusView;
    String bookedDate,orderID,tokenID,paymentID;
    Boolean status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Intent getStatus = getIntent();
        status = getStatus.getBooleanExtra("Status",false);
                    /*
    There are three PNG files in the drawables folder: one each for success, error and pending.
    TODO: Depending on what the status returned by payTM is, set the appropriate image and display status message


     */


        GoBackbutton = findViewById(R.id.GoBackButton);

        //TODO : Manipulate the following views in accordance with the status returned by PayTM.
        //Side note : prefer to use "hh:mm,  dd-mm-yyyy" for displaying time of completion.
        StatusView = findViewById(R.id.payment_status);
        StatusHeader = findViewById(R.id.status_header);
        StatusMessage = findViewById(R.id.status_message);
        BookingID = findViewById(R.id.ID_value);
        StatusView = findViewById(R.id.payment_status);
        TimeOfBooking = findViewById(R.id.time_value);
        bookedDate=AppointmentActivity.selectedDate;
        if(status==true)
        {
            StatusView.setImageResource(R.drawable.success);
            orderID=getStatus.getStringExtra("OrderID");
            paymentID=getStatus.getStringExtra("PaymentID");
            tokenID=getStatus.getStringExtra("PaymentToken");
            BookingID.setText(paymentID);
            TimeOfBooking.setText(bookedDate);
            StatusMessage.setText("You have successfully made a booking");
            StatusHeader.setText("SUCCESS!");
        }
        else
        {
            StatusView.setImageResource(R.drawable.error);
            StatusHeader.setText("ERROR!");
            StatusMessage.setText("Payment Failed! Please Try Again!");
            BookingID.setVisibility(View.INVISIBLE);
            TimeOfBooking.setVisibility(View.INVISIBLE);
        }

        //TextView StatusHeader, StatusMessage, BookingID, TimeOfBooking;
        GoBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusActivity.this, DoctorsActivity.class));
                finish();
            }
        });

    }






}
