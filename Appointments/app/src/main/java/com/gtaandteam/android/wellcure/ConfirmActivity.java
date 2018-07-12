package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class ConfirmActivity extends AppCompatActivity {


    Button ConfirmButton;
    TextView DoctorNameTV,
            DateTV,
            PatientNameTV,
            EmailTV,
            AmountTV;
    ImageView DoctorPhoto;
    String name,date,email,purpose,amount,phone;



    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("EmailTV", email);
            pay.put("PhoneNumber", phone);
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

        ConfirmButton = findViewById(R.id.PayButton);
        DoctorNameTV = findViewById(R.id.DoctorName);
        DateTV = findViewById(R.id.DateValue); //DateTV of appointment
        PatientNameTV = findViewById(R.id.NameValue);
        EmailTV = findViewById(R.id.EmailValue);
        AmountTV = findViewById(R.id.Amount); //AmountTV to be paid
        DoctorPhoto = findViewById(R.id.profile_photo);

        Intent getDetails = getIntent();

        name=getDetails.getStringExtra("NameET");
        date=getDetails.getStringExtra("DateTV");
        phone=getDetails.getStringExtra("PhoneET");
        email=getDetails.getStringExtra("EmailET");

        PatientNameTV.setText(name);
        EmailTV.setText(email);
        DateTV.setText(date);

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
