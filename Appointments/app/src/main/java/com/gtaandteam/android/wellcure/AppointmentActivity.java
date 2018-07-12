package com.gtaandteam.android.wellcure;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/*TODO: Auto-enter Details Added. Should add getting details from Main Screen*/

public class AppointmentActivity extends AppCompatActivity {

    EditText DateET,
            NameET,
            PhoneET,
            EmailET;
    Button BookAndPayButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth FbAuth;
    FirebaseUser NewUser;
    Toolbar toolbar;
    String FirstName,
            Email,
            PhoneNumber,
            Date;
    static String SelectedDate, TodaysDate;

    String rName, rEmail, rPhone, rDate; //retrieved files from database

    HashMap<String, String> data;
    DatabaseReference userDb1,userDb2,appointmentDb;
    int year, month, day;
    private ProgressDialog Progress;
    Boolean userExists;
    TextView BookingTypeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        FbAuth = FirebaseAuth.getInstance();
        NewUser =FirebaseAuth.getInstance().getCurrentUser();
        NameET = findViewById(R.id.name_editText);
        PhoneET = findViewById(R.id.phone_editText);
        EmailET = findViewById(R.id.email_editText);
        DateET = findViewById(R.id.date_editText);
        BookingTypeTV = findViewById(R.id.BookingType);
        BookAndPayButton = findViewById(R.id.bookAndPay_Button);
        toolbar = findViewById(R.id.my_toolbar);

        Intent i=getIntent();
        userExists=i.getBooleanExtra("userExists",false);
        Progress =new ProgressDialog(this);
        if(userExists==true)
        {
            Progress.setMessage("Loading Details of User");
            Progress.show();
        }

        BookAndPayButton.setVisibility(View.INVISIBLE);
                DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day= cal.get(Calendar.DAY_OF_MONTH);
                TodaysDate =day+"/"+(month+1)+"/"+year;
                DatePickerDialog dialog = new DatePickerDialog(
                        AppointmentActivity.this, android.R.style.Theme_Holo_Dialog,mDateSetListener,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateET.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                SelectedDate =dayOfMonth+"/"+(month+1)+"/"+year;
                Date endDate = new Date(year+"/"+(month+1)+"/"+dayOfMonth);
                Date startDate = Calendar.getInstance().getTime();
                long duration  = endDate.getTime() - startDate.getTime();
                long diffday=duration/(24 * 60 * 60 * 1000) +1;
                int days=(int)diffday-1;
                if(days<0) {
                    BookingTypeTV.setVisibility(View.VISIBLE);
                    BookingTypeTV.setText("Appointment DateTV Has Already Passed\nChoose A New DateTV");
                    BookAndPayButton.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getBaseContext(), "Appointment DateTV Has Already Passed", Toast.LENGTH_SHORT).show();
                }
                else {
                    BookingTypeTV.setVisibility(View.VISIBLE);
                    BookingTypeTV.setText("No. of Days till Appointment : "+days);
                    BookAndPayButton.setVisibility(View.VISIBLE);
                    //Toast.makeText(getBaseContext(),"Days Gap "+ days,Toast.LENGTH_SHORT).show();
                }

            }
        };


        BookAndPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("App","Clicked Submit");
                storeData();
            }
        });


        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        retrieve();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_SignOut:
                // Handle user sign out.
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(AppointmentActivity.this, R.style.AlertDialogCustom)
                );
                builder.setCancelable(true);
                builder.setTitle("Sign Out");
                builder.setMessage("Are you sure you want to sign out?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Signing Out", Toast.LENGTH_LONG).show();

                            }
                        });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            case R.id.action_past:
                startActivity(new Intent(AppointmentActivity.this, PastActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public void signOut() {
        FbAuth.signOut();
    }

 //STORE USER DATA IN DATABASE//
    public void storeData(){
        Log.v("App","Entered FUnction");
        FirstName = NameET.getText().toString();
        //second_name=etSecondName.getText().toString();
        Email = EmailET.getText().toString();
        PhoneNumber = PhoneET.getText().toString();
        Date =day+"/"+(month+1)+"/"+year;
        data= new HashMap<>();
        data.put("NameET", FirstName);
        data.put("EmailET", Email);
        data.put("PhoneET", PhoneNumber);
        data.put("LoginDate", Date);
        Log.v("App","Hashmap Done");
        userDb1 = FirebaseDatabase.getInstance().getReference().child("users");
        userDb1.child(FbAuth.getCurrentUser().getUid()).setValue(data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //finish();
                //go to page which shows users details
                Log.v("App","Done Shit");
                Toast.makeText(getApplicationContext(),"Stored Data",Toast.LENGTH_SHORT).show();
                Intent toConfirm = new Intent(AppointmentActivity.this, ConfirmActivity.class);
                toConfirm.putExtra("NameET", FirstName);
                toConfirm.putExtra("EmailET", Email);
                toConfirm.putExtra("PhoneET", PhoneNumber);
                toConfirm.putExtra("DateTV", SelectedDate);
                startActivity (toConfirm);
            }
        });
    }
    

    public void retrieve() {

        userDb2 = FirebaseDatabase.getInstance().getReference("users").child(FbAuth.getCurrentUser().getUid());
        userDb2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                String ss = dataSnapshot.getKey().toString();
                if (ss.equals("NameET")) {
                    rName= dataSnapshot.getValue().toString();
                } else if (ss.equals("EmailET"))
                    rEmail= dataSnapshot.getValue().toString();
                else if (ss.equals("PhoneET"))
                    rPhone= dataSnapshot.getValue().toString();
                else if (ss.equals("DateTV"))
                    rDate= dataSnapshot.getValue().toString();
                NameET.setText(rName);
                EmailET.setText(rEmail);
                PhoneET.setText(rPhone);
                Progress.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


}
}
