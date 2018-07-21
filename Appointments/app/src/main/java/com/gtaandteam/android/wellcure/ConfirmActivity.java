package com.gtaandteam.android.wellcure;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class ConfirmActivity extends AppCompatActivity {

    /**Data Structures*/
    String Name, Date, Email, Purpose, Amount, Phone;

    /**Views*/
    Button ConfirmBTN;
    TextView DoctorNameTV, DateTV, PatientNameTV, EmailTV, AmountTV;
    ImageView DoctorIV;

    InstapayListener listener;

    final String LOG_TAG = this.getClass().getSimpleName();

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
            Log.e("APP", "JSONException was caught");
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }
    

    
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
                onSuccess.putExtra("Amount",Amount);
                onSuccess.putExtra("Name",Name);

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

        //Linking to views
        ConfirmBTN = findViewById(R.id.PayBTN);
        DoctorNameTV = findViewById(R.id.DoctorNameTV);
        DateTV = findViewById(R.id.DateET); //DateTV of appointment
        PatientNameTV = findViewById(R.id.NameET);
        EmailTV = findViewById(R.id.EmailET);
        AmountTV = findViewById(R.id.AmountTV); //AmountTV to be paid
        DoctorIV = findViewById(R.id.DoctorIV);

        Intent getDetails = getIntent();

        Name =getDetails.getStringExtra("Name");
        Date =getDetails.getStringExtra("Date");
        Phone =getDetails.getStringExtra("Phone");
        Email =getDetails.getStringExtra("Email");

        PatientNameTV.setText(Name);
        EmailTV.setText(Email);
        DateTV.setText(Date);

        Purpose = "Wellcure Clinic Appointment Fee";
        Amount ="10";

        ConfirmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callInstamojoPay(Email, Phone, Amount, Purpose, Name);
            }
        });
    }
}
