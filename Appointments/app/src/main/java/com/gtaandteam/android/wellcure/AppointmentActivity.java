package com.gtaandteam.android.wellcure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class AppointmentActivity extends AppCompatActivity {

    /**Data Structures*/
    int Year, Month, Day;
    String FirstName, Email, PhoneNumber, Date;
    String rName, Amount; //retrieved files from database
    HashMap<String, String> Data;
    static String SelectedDate, TodaysDate;
    Boolean UserExists;

    /**Views*/
    EditText DateET, NameET, PhoneET, EmailET;
    TextView BookingTypeTV;
    Button BookAndPayBTN;
    Toolbar MyToolbar;

    /**Firebase*/
    FirebaseUser NewUser;
    private ProgressDialog Progress;
    static ProgressDialog Progress2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth FbAuth;
    DatabaseReference UserDb1, UserDb2, AppointmentDb;

    final String LOG_TAG = this.getClass().getSimpleName();
    String latestDate="";
    String LatestDate="";
    Date lastAppointment,todays,requestedApt;
    SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //Linking to views
        FbAuth = FirebaseAuth.getInstance();
        NewUser =FirebaseAuth.getInstance().getCurrentUser();
        NameET = findViewById(R.id.NameET);
        PhoneET = findViewById(R.id.PhoneET);
        EmailET = findViewById(R.id.EmailET);
        DateET = findViewById(R.id.DateET);
        BookingTypeTV = findViewById(R.id.BookingType);
        BookAndPayBTN = findViewById(R.id.bookAndPay_Button);
        MyToolbar = findViewById(R.id.MyToolbar);
        EmailET.setFocusable(false);
        PhoneET.setFocusable(false);

        Intent intent = getIntent();
        UserExists = intent.getBooleanExtra("UserExists",false);
        Progress = new ProgressDialog(this);
        Progress2 = new ProgressDialog(this);
        //Toast.makeText(AppointmentActivity.this, "Value of UserExists : "+UserExists, Toast.LENGTH_SHORT).show();
        Progress.setMessage("Loading Details of User");
        Progress.setCancelable(false);
        Progress.show();

        BookAndPayBTN.setVisibility(View.GONE); //In order to avoid invalid inputs
        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Year = cal.get(Calendar.YEAR);
                Month = cal.get(Calendar.MONTH);
                Day = cal.get(Calendar.DAY_OF_MONTH);
                TodaysDate = Day +"/"+(Month +1)+"/"+ Year;
                DatePickerDialog dialog = new DatePickerDialog(
                        AppointmentActivity.this, android.R.style.Theme_Holo_Dialog,mDateSetListener, Year, Month, Day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+(month+1)+"/"+year;
                DateET.setText(date);
                SelectedDate =dayOfMonth+"/"+(month+1)+"/"+year;
                Date endDate = new Date(year+"/"+(month+1)+"/"+dayOfMonth); //Deprecation Warning [Date].
                requestedApt=endDate;
                Date startDate = Calendar.getInstance().getTime();
                todays=startDate;
                long duration  = endDate.getTime() - startDate.getTime();
                long diffday = duration/(24 * 60 * 60 * 1000) +1;
                int days =(int)diffday-1;
                if(days<0) {
                    BookingTypeTV.setText("Appointment Date has already passed\n\nChoose A New Date");
                    BookAndPayBTN.setVisibility(View.INVISIBLE);
                }
                else if(days==0)
                {
                    BookingTypeTV.setText("Appointment Date Should Be \nMinimum 2 Days Later\n\nChoose A New Date");
                    BookAndPayBTN.setVisibility(View.INVISIBLE);
                }
                else {
                    BookingTypeTV.setText("Number of Days till Appointment: "+days);
                    BookAndPayBTN.setVisibility(View.VISIBLE);
                }


            }
        };

        EmailET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AppointmentActivity.this, "Not Allowed To Change Email", Toast.LENGTH_SHORT).show();
            }
        });

        PhoneET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AppointmentActivity.this, "Not Allowed To Change Phone Number", Toast.LENGTH_SHORT).show();
            }
        });

        BookAndPayBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Calculate Amount To Be Paid based on Past Appointments.
                Log.v("APP","Clicked Submit");
                FirstName = NameET.getText().toString().trim();
                if (TextUtils.isEmpty(FirstName)) {
                    // is empty
                    Toast.makeText(AppointmentActivity.this, "Name Field Cannot Be Blank", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Name Field Is Blank");

                } else {
                    try
                    {
                        if(!isConnected()) {
                            Snackbar sb = Snackbar.make(view, "No Internet Connectivity", Snackbar.LENGTH_LONG);
                            sb.getView().setBackgroundColor(getResources().getColor(R.color.darkred));
                            sb.show();
                            Log.d(LOG_TAG,"No Internet");
                            return;
                        }
                        else
                        {
                            Log.d(LOG_TAG,"Internet is connected");
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG,"Exception : "+e.getMessage());
                    }

                    format = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        lastAppointment = format.parse(LatestDate);
                        Log.d(LOG_TAG,"Converted Date : "+lastAppointment.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long duration  = requestedApt.getTime() - lastAppointment.getTime();
                    long diffday = duration/(24 * 60 * 60 * 1000) +1;
                    int days =(int)diffday-1;
                    Log.d(LOG_TAG,"No of days between appointments : "+days);
                    if(days>31)
                    {
                        Log.d(LOG_TAG,"Booking New Appointment");
                        Amount="300";
                    }
                    else if(days<0)
                    {
                        Log.d(LOG_TAG,"An appointment is already there ahead");
                        Amount="300";
                    }
                    else if(days==0)
                    {
                        Log.d(LOG_TAG,"An appointment is already scheduled for same day");
                        Toast.makeText(AppointmentActivity.this, "You already have an appointment for that day", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        Log.d(LOG_TAG,"Booking Follow Up Appointment");
                        Amount="150";
                    }
                    storeData();
                }
            }
        });


        setSupportActionBar(MyToolbar);
        // Get a support ActionBar corresponding to this MyToolbar
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
                /** Handle user sign out. */

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
                Intent intent = new Intent(AppointmentActivity.this, PastActivity.class);
                intent.putExtra("Parent", LOG_TAG);
                startActivity(intent);
                return true;
            case R.id.action_About:
                startActivity(new Intent(AppointmentActivity.this, AboutActivity.class));
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

    public void storeData(){
        /**Stores user data in the database*/

        Log.d(LOG_TAG,"Entered storeData() Function");
        //FirstName = NameET.getText().toString().trim();
        //second_name=etSecondName.getText().toString();
        Progress2.setMessage("Loading Confirmation");
        Progress.setCancelable(false);
        Progress2.show();
        rName=FbAuth.getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(rName)) {
            updateDisplayName(FirstName);

        }
        Date = Day +"/"+(Month +1)+"/"+ Year;
        Data = new HashMap<>();
        Data.put("Name", FirstName);
        Data.put("Email", Email);
        Data.put("Phone", PhoneNumber);
        Data.put("LoginDate", Date);
        Log.d(LOG_TAG,"Hashmap Done");
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("users");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                //finish();
                //go to page which shows users details
                Log.d(LOG_TAG,"Stored to Database");
                Toast.makeText(getApplicationContext(),"Stored Data",Toast.LENGTH_SHORT).show();
                Intent toConfirm = new Intent(AppointmentActivity.this, ConfirmActivity.class);
                toConfirm.putExtra("Name", FirstName);
                toConfirm.putExtra("Email", Email);
                toConfirm.putExtra("Phone", PhoneNumber);
                toConfirm.putExtra("Date", SelectedDate);
                toConfirm.putExtra("Amount",Amount);
                startActivity (toConfirm);
            }
        });
    }


    public void retrieve() {

        /**Function to Auto-fill information about a previously registered user.*/

        rName="";
        rName=FbAuth.getCurrentUser().getDisplayName();
            PhoneNumber = FbAuth.getCurrentUser().getPhoneNumber().substring(3);
            Email=FbAuth.getCurrentUser().getEmail();
        NameET.setText(rName);
        EmailET.setText(Email);
        PhoneET.setText(PhoneNumber);
        //getLatestAppointment();

        try
        {
            if(!(rName.equals("")))
            {
                getLatestDate();
                Log.d(LOG_TAG,"rName not equal blank");
            }
        }
        catch (Exception e)
        {
            rName="";
            Log.d(LOG_TAG,"Inside rName exception : "+e.getMessage());
            Amount="300";
        }


        Log.d(LOG_TAG,"Amount outside all: "+Amount);

        Progress.dismiss();


    }

    private void updateDisplayName(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User profile updated.");
                        }
                    }
                });
    }
    public void getLatestDate(){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointmentDB");//.child();
        final Query lastQuery = databaseReference.child(FbAuth.getCurrentUser().getUid()).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap map=(HashMap) dataSnapshot.getValue();
                try
                {
                    /*for ( Object key : map.keySet() ) {
                        latestDate=key.toString();

                    }*/
                    Object key = map.keySet();
                    LatestDate=key.toString();
                    Log.d(LOG_TAG,"rName : "+rName);
                    Log.d(LOG_TAG,"Latest Date : "+LatestDate);
                    try{
                        if(LatestDate.equals("")||LatestDate==null)
                        {
                            Amount ="300";
                            Log.d(LOG_TAG,"Amount in try: "+Amount);
                        }

                    }
                    catch (Exception e)
                    {
                        Amount="350";
                        Log.d(LOG_TAG,"Amount in catch: "+Amount);
                        Log.d(LOG_TAG,"Error : "+e.getMessage());
                    }

                }
                catch(Exception e)
                {
                    Log.d(LOG_TAG,"Error inside getLatestAppt: "+e.getMessage());
                }
                //Do calculations here to find month diffrence
                try{
                    StringBuilder builder = new StringBuilder(LatestDate);
                    builder.deleteCharAt(0);
                    builder.deleteCharAt(LatestDate.length() - 2);
                    latestDate=builder.toString();
                    LatestDate=latestDate.replaceAll("-", "/");
                    Log.d(LOG_TAG,"Done fetching Latest Date : "+latestDate);
                }
                catch (Exception e)
                {
                    Log.d(LOG_TAG,"Error : "+e.getMessage());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
                Log.d(LOG_TAG,databaseError.getMessage());
            }
        });
        //this may return null

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(LOG_TAG, "back button pressed");
        }
        if(isTaskRoot())
        {
            Log.d(LOG_TAG,"No other Activities Exist");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(AppointmentActivity.this, R.style.AlertDialogCustom));
            builder.setCancelable(true);
            builder.setTitle("Exit App");
            builder.setMessage("Are you sure you want to Exit?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"Exiting App");
                            finishAffinity();

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
        }
        else
        {
            Log.d(LOG_TAG,"Other Activities Exist");
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()) {
                    d.dismiss();
                    Toast.makeText(AppointmentActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_SHORT).show();
                }
            }
        }, time);
    }


}
    
    
    
    /*
   public void getLatestAppointment() {
        //Not tested yet....(Change to Direction.Acending if required)
       /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDB");
       userRef.orderByChild("Phone").equalTo(PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() { 

       UserDb2 = FirebaseDatabase.getInstance().getReference("users").child(FbAuth.getCurrentUser().getUid());
        UserDb2.orderByChild("date").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


                });
    }*/
    

