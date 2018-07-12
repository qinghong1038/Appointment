package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.app.Activity;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.IntentFilter;
import android.widget.Toast;
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
    String name,date,email,purpose,amount,phone;



    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
 } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }
    
    InstapayListener listener;

    
    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                String payInfo[]=response.split(":");
                String orderID[]=payInfo[1].split("=");;
                String paymentID[]=payInfo[3].split("=");;
                String paymentToken[]=payInfo[4].split("=");;

                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();


                Intent onSuccess = new Intent(ConfirmActivity.this, StatusActivity.class);
                onSuccess.putExtra("Status",true);
                onSuccess.putExtra("OrderID",orderID[1]);
                onSuccess.putExtra("PaymentID",paymentID[1]);
                onSuccess.putExtra("PaymentToken",paymentToken[1]);
                startActivity(onSuccess);
                finish();
            }

            @Override
            public void onFailure(int code, String reason) {
                //Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG).show();

                Intent onFailure = new Intent(ConfirmActivity.this, StatusActivity.class);
                onFailure.putExtra("Status",false);
                startActivity(onFailure);
                finish();
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Intent getDetails = getIntent();

        name=getDetails.getStringExtra("Name");
        date=getDetails.getStringExtra("Date");
        phone=getDetails.getStringExtra("Phone");
        email=getDetails.getStringExtra("Email");

        ConfirmButton = findViewById(R.id.PayButton);
        DoctorName = findViewById(R.id.DoctorName);
        Date = findViewById(R.id.DateValue); //Date of appointment
        PatientName = findViewById(R.id.NameValue);
        Email = findViewById(R.id.EmailValue);
        Amount = findViewById(R.id.Amount); //Amount to be paid
        DoctorPhoto = findViewById(R.id.profile_photo);
        PatientName.setText(name);
        Email.setText(email);
        Date.setText(date);

        purpose = "Wellcure Clinic Appointment Fee";
        amount ="10";

        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callInstamojoPay(email,phone,amount,purpose,name);
            }
        });
    }
}
