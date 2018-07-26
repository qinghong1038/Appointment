package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    /**Data Structures*/
    String BookingDate, OrderID, TokenID, PaymentID, AppointmentDate, Name, Amount;
    Boolean Success;
    HashMap<String, String> Data;

    /**Views*/
    Button GoBackBTN;
    TextView StatusHeaderTV, StatusMessageTV, BookingIDTV, TimeOfBookingTV, TimeOfAppointmentTV;
    ImageView StatusIV;
    Appointment newAppointment;

    DatabaseReference UserDb1;
    private FirebaseAuth FbAuth;

    final String LOG_TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        FbAuth = FirebaseAuth.getInstance();
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
        Name = getStatus.getStringExtra("Name");
        Amount=getStatus.getStringExtra("Amount");

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
            addAppointmentToDatabase();

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
    private void addAppointmentToDatabase()
    {
        String AppointmentDate,PatientName,DoctorName;
        Float Fees;
        Toast.makeText(this, "Appointment Details Stored To AppointmentDatabase", Toast.LENGTH_SHORT).show();
        AppointmentDate = AppointmentActivity.SelectedDate;
        PatientName = Name;
        DoctorName = DoctorsActivity.DoctorName;
        Fees = Float.parseFloat(Amount);
        Log.d(LOG_TAG, "Amount is "+Fees);
        //Data=new HashMap<>();
        //Data.put("")
        //TODO:READ THIS
        newAppointment = new Appointment(PatientName,DoctorName,AppointmentDate,Fees);
        String date=AppointmentDate;
        date.replaceAll("/", "-");
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("appointmentDB");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).child(date).setValue(newAppointment).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //finish();
                //go to page which shows users details
                Log.d(LOG_TAG,"New Appointment Added To Database");
                Toast.makeText(getApplicationContext(),"Stored Appointment Data",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
