package com.gtaandteam.android.wellcure;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    /**Data Structures*/
    String BookingDate, OrderID, TokenID, PaymentID, AppointmentDate, Name, Amount,Reason;
    String Email,Phone;

    int Code;
    Boolean Success;
    HashMap<String, String> Data;

    /**Views*/
    Button GoBackBTN;
    TextView StatusHeaderTV, StatusMessageTV, BookingIDTV, TimeOfBookingTV, TimeOfAppointmentTV,BookingLabel,BookingDateTV,AppointmentDateTV;
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
        BookingLabel = findViewById(R.id.BookingIDTV);
        BookingDateTV = findViewById(R.id.BookingDateTV);
        AppointmentDateTV = findViewById(R.id.AppointmentDateTV);

        //Side note : prefer to use "hh:mm,  dd-mm-yyyy" for displaying time of completion.
        Intent getStatus = getIntent();
        Email=getStatus.getStringExtra("Email");
        Phone=getStatus.getStringExtra("Phone");
        Success = getStatus.getBooleanExtra("Status",false);
        Name = getStatus.getStringExtra("Name");
        Amount=getStatus.getStringExtra("Amount");
        OrderID=getStatus.getStringExtra("OrderID");
        PaymentID=getStatus.getStringExtra("PaymentID");
        TokenID=getStatus.getStringExtra("PaymentToken");
        Reason=getStatus.getStringExtra("Reason");
        Code=getStatus.getIntExtra("Code",0);


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
            Answers.getInstance().logPurchase(new PurchaseEvent()
                    .putItemPrice(BigDecimal.valueOf(Double.parseDouble(Amount)))
                    .putCurrency(Currency.getInstance("INR"))
                    .putItemName(AppointmentActivity.aptType)
                    .putSuccess(true));

        }
        else
        {
            StatusIV.setImageResource(R.drawable.error);
            StatusHeaderTV.setText("ERROR!");
            StatusMessageTV.setText("Payment Failed! Please Try Again!");
            BookingIDTV.setVisibility(View.INVISIBLE);
            TimeOfBookingTV.setVisibility(View.INVISIBLE);
            TimeOfAppointmentTV.setVisibility(View.INVISIBLE);
            BookingLabel.setVisibility(View.INVISIBLE);
            BookingDateTV.setVisibility(View.INVISIBLE);
            AppointmentDateTV.setVisibility(View.INVISIBLE);
            Answers.getInstance().logPurchase(new PurchaseEvent()
                    .putItemPrice(BigDecimal.valueOf(Double.parseDouble(Amount)))
                    .putCurrency(Currency.getInstance("INR"))
                    .putItemName(AppointmentActivity.aptType)
                    .putSuccess(false));


        }
        storeData();

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
        String PatientName,DoctorName;
        Float Fees;
        Toast.makeText(this, "Appointment Details Stored To AppointmentDatabase", Toast.LENGTH_SHORT).show();
        AppointmentDate = AppointmentActivity.SelectedDate;
        PatientName = Name;
        DoctorName = DoctorsActivity.DoctorName;
        Fees = Float.parseFloat(Amount);
        Log.d(LOG_TAG, "Amount is "+Fees);
        newAppointment = new Appointment(PatientName,DoctorName,AppointmentDate,BookingDate, Fees);
        String date;
        date=AppointmentDate.replaceAll("/", "-");
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String timeStamp=String.valueOf(today.hour)
                +String.valueOf(today.minute)+String.valueOf(today.second);
        date=date+','+timeStamp;
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

    public void storeData(){
        /**Stores user data in the database*/

        Log.d(LOG_TAG,"Entered storeData() Function");

        /*onSuccess.putExtra("Status",true);
        getStatus.getStringExtra("OrderID",orderID[1]);
        getStatus.getStringExtra("PaymentID",paymentID[1]);
        getStatus.getStringExtra("PaymentToken",paymentToken[1]);
        getStatus.getStringExtra("Amount",Amount);
        getStatus.getStringExtra("Name",Name);*/
        Data = new HashMap<>();
        Data.put("Name",Name);
        Data.put("Email",Email);
        Data.put("Phone",Phone);
        Data.put("AppointmentDate",AppointmentActivity.SelectedDate);
        Data.put("BookingDate",AppointmentActivity.TodaysDate);
        Data.put("Amount", Amount);
        if(Success)
        {
            Data.put("OrderID", OrderID);
            Data.put("PaymentID", PaymentID);
            Data.put("PaymentToken", TokenID);

        }
        else
        {
            Data.put("OrderID", "ERROR");
            Data.put("PaymentID", "Fail Code : "+Code);
            Data.put("PaymentToken", "Reason : "+Reason);

        }
        Log.d(LOG_TAG,"Hashmap Done");
        String date;
        date=AppointmentActivity.TodaysDate.replaceAll("/", "-");
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        date=date+","+String.valueOf(today.hour)+String.valueOf(today.minute)+String.valueOf(today.second);
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("txnDetails");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).child(date).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //finish();
                //go to page which shows users details
                Log.d(LOG_TAG,"Stored TXN Details to Database");
                Toast.makeText(getApplicationContext(),"Stored Data",Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");
        }
        if(isTaskRoot())
        {
            Log.d(LOG_TAG,"No other Activities Exist");
        }
        else
        {
            Log.d(LOG_TAG,"Other Activities Exist");
        }
        Intent intent = new Intent(StatusActivity.this, AppointmentActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(StatusActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,""+e.getMessage());
                }
            }
        }, time);
    }

}
