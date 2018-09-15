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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;


public class AppointmentActivity extends AppCompatActivity {

    /**Data Structures*/
    int Year, Month, Day;
    String FirstName, Email, PhoneNumber, Date;
    String rName, Amount;
    static Double FinalAmount,DoubleAmount,DoubleFollowUp; //retrieved files from database
    static String aptType="";
    HashMap<String, String> Data;
    static String SelectedDate, TodaysDate;
    Boolean UserExists,newApt=true;
    static Boolean PromoApplied;
    static String PromoValue="";
    long min=Integer.MAX_VALUE;
    /**Views*/
    EditText DateET, NameET, PhoneET, EmailET,PromoCodeET;
    TextView BookingTypeTV;
    Button BookAndPayBTN;
    RadioGroup RGroup;
    RadioButton newRB, FollowUpRB;
    Toolbar MyToolbar;
    ArrayList<String> dates= new ArrayList<>();
    /**Firebase*/
    FirebaseUser NewUser;
    private ProgressDialog Progress;
    static ProgressDialog Progress2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth FbAuth;
    DatabaseReference UserDb1, UserDb2, AppointmentDb;
    //deduction from promocode//
    double deduction;
    static String PromoCode;
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
        PromoCodeET = findViewById(R.id.PromoCodeET);
        BookingTypeTV = findViewById(R.id.BookingMessage);
        BookAndPayBTN = findViewById(R.id.bookAndPay_Button);
        MyToolbar = findViewById(R.id.MyToolbar);
        //EmailET.setFocusable(false);
        //PhoneET.setFocusable(false);

        /**NEW BUTTONS*/
        newRB = findViewById(R.id.new_appt);
        FollowUpRB = findViewById(R.id.follow_up_appt);
        RGroup = findViewById(R.id.Radio_group);
        PromoApplied=false;
        FinalAmount=0.0;
        DoubleAmount=0.0;
        DoubleFollowUp=0.0;

        Intent intent = getIntent();
        UserExists = intent.getBooleanExtra("UserExists",false);

        Progress = new ProgressDialog(this);
        Progress2 = new ProgressDialog(this);
        //Toast.makeText(AppointmentActivity.this, "Value of UserExists : "+UserExists, Toast.LENGTH_SHORT).show();
        Progress.setMessage("Loading Details of User");
        Progress.setCancelable(false);
        Progress.show();
        timerDelayRemoveDialog(30000,Progress);

        BookAndPayBTN.setVisibility(View.GONE); //In order to avoid invalid inputs
        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Year = cal.get(Calendar.YEAR);

                Month = cal.get(Calendar.MONTH);
                String mnth=String.valueOf(Month+1);
                if(mnth.length()==1){mnth="0"+mnth;}

                Day = cal.get(Calendar.DAY_OF_MONTH);
                String date=String.valueOf(Day);
                if(date.length()==1){date="0"+date;}


                TodaysDate = date +"/"+mnth+"/"+ Year;
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
                String day=String.valueOf(dayOfMonth);
                String month_= String.valueOf(month+1);
                if(day.length()==1)
                    day="0"+day;
                if(month_.length()==1)
                    month_="0"+month_;
                SelectedDate=day+"/"+(month_)+"/"+year;
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

                    int date1=endDate.getDate();
                    int date2=startDate.getDate();
                    int diff=(date1-date2);
                    if(diff==0)
                    {
                        BookingTypeTV.setText("Booking Appointment for Today");
                    }
                    else if(diff==1)
                        BookingTypeTV.setText("Booking Appointment for Tomorrow");
                    BookAndPayBTN.setVisibility(View.VISIBLE);
                }
                else {
                    BookingTypeTV.setText("Number of Days till Appointment: "+days);
                    BookAndPayBTN.setVisibility(View.VISIBLE);
                }


            }
        };

        BookAndPayBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Calculate Amount To Be Paid based on Past Appointments.
                Log.v("APP","Clicked Submit");
                PromoApplied=false;
                FirstName = NameET.getText().toString().trim();
                PhoneNumber=PhoneET.getText().toString().trim();
                if (TextUtils.isEmpty(FirstName)) {
                    // is empty
                    Toast.makeText(AppointmentActivity.this, "Name Field Cannot Be Blank", Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "Name Field Is Blank");
                    return;

                }
                if (TextUtils.isEmpty(PhoneNumber)) {
                    // is empty
                    Toast.makeText(AppointmentActivity.this, "Phone Field Cannot Be Blank", Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "Phone Field Is Blank");
                    return;

                }
                if(PhoneNumber.length()!=10)
                {
                    Toast.makeText(AppointmentActivity.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "Phone Field Is Not 10 Digit Long");
                    return;

                }
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

                format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    lastAppointment = format.parse(LatestDate);
                    Log.d(LOG_TAG,"Converted Date : "+lastAppointment.toString());
                } catch (ParseException e) {
                    Log.d(LOG_TAG,"LatestDateError : "+e.getMessage());

                    if(FollowUpRB.isChecked())
                    {
                        Toast.makeText(AppointmentActivity.this, "Follow Up Appointment Cannot Be Booked since No Appointment Was Taken in the Last 30 Days", Toast.LENGTH_LONG).show();
                        newRB.setChecked(true);
                        return;
                    }
                }
                if(newRB.isChecked())
                {
                    Progress2.setMessage("Preparing Your Order");
                    Progress2.setCancelable(false);
                    Progress2.show();
                    timerDelayRemoveDialog(30000,Progress2);
                    //Amount="300";
                    aptType="New Appointment";
                    UserDb2 = FirebaseDatabase.getInstance().getReference().child("fees");
                    UserDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Amount= dataSnapshot.getValue().toString();
                            Log.d(LOG_TAG,"NewApptAmount : "+Amount);
                            if(Amount==null)
                                Amount="300";
                            DoubleAmount=Double.parseDouble(Amount);
                            FinalAmount=DoubleAmount;
                            Log.d(LOG_TAG,"Final Amount Before Promo : "+FinalAmount);
                            Log.d(LOG_TAG,"Type of Apt : "+aptType);
                            PromoCode=PromoCodeET.getText().toString().toUpperCase();
                            Progress2.dismiss();
                            if(!PromoCode.trim().equals(""))
                            {
                                Progress2.setMessage("Checking PromoCode");
                                Progress2.setCancelable(false);
                                Progress2.show();
                                timerDelayRemoveDialog(30000,Progress2);
                                UserDb2 = FirebaseDatabase.getInstance().getReference().child("promoCodes");
                                UserDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        HashMap map=(HashMap) dataSnapshot.getValue();
                                        for (Object code: map.keySet()){
                                            //Log.d(LOG_TAG,"Promo Code : "+code.toString()+" Value : "+map.get(code.toString()).toString());
                                            if(PromoCode.equals(code.toString())){
                                                Progress2.dismiss();
                                                deduction=Double.parseDouble(map.get(code.toString()).toString());
                                                PromoValue=""+deduction;
                                                Log.d(LOG_TAG,"Deduction : "+deduction);
                                                FinalAmount=DoubleAmount-deduction;
                                                Log.d(LOG_TAG,"Promo Code : "+PromoCode+" applied!");
                                                Toast.makeText(AppointmentActivity.this, "Promo Code : "+PromoCode+" applied!", Toast.LENGTH_SHORT).show();
                                                Log.d(LOG_TAG,"Final Amount for CheckOut : "+FinalAmount);
                                                PromoApplied=true;
                                                Answers.getInstance().logAddToCart(new AddToCartEvent()
                                                        .putItemPrice(BigDecimal.valueOf(FinalAmount))
                                                        .putCurrency(Currency.getInstance("INR"))
                                                        .putItemName(aptType));
                                                storeData();
                                            }
                                        }
                                        if(!PromoApplied) {
                                            Progress2.dismiss();
                                            Log.d(LOG_TAG,"No Such PromoCode");
                                            Toast.makeText(AppointmentActivity.this, "No Such Promo Code", Toast.LENGTH_LONG).show();
                                            //PromoCodeET.selectAll();
                                            PromoCodeET.setSelectAllOnFocus(true);
                                            PromoCodeET.clearFocus();
                                        }



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(LOG_TAG,"Error PromoCode : "+databaseError.getMessage());
                                    }
                                });
                            }
                            else
                            {
                                PromoApplied=false;
                                Log.d(LOG_TAG,"Final Amount for CheckOut : "+FinalAmount);
                                Answers.getInstance().logAddToCart(new AddToCartEvent()
                                        .putItemPrice(BigDecimal.valueOf(DoubleAmount))
                                        .putCurrency(Currency.getInstance("INR"))
                                        .putItemName(aptType));
                                storeData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(LOG_TAG,"Error : "+databaseError.getMessage());
                        }
                    });

                }
                if(FollowUpRB.isChecked()) {
                    Progress2.setMessage("Preparing Your Order");
                    Progress2.setCancelable(false);
                    Progress2.show();
                    timerDelayRemoveDialog(30000,Progress2);
                    for (int i = 0; i < dates.size(); i++)
                    {
                        try {
                            lastAppointment = format.parse(dates.get(i));
                        }catch(Exception e){}

                        long duration = requestedApt.getTime() - lastAppointment.getTime();
                        long diffday = duration / (24 * 60 * 60 * 1000) + 1;
                        int days = (int) diffday - 1;
                        if(days<0)
                        {
                            Toast.makeText(AppointmentActivity.this, "Follow Up Appointment Cannot Be Booked since No Appointment Was Taken in the Last 30 Days", Toast.LENGTH_LONG).show();
                            newRB.setChecked(true);
                            return;
                        }

                        Log.d(LOG_TAG, "No of days between appointments : " + days);
                        if(days<min)
                            min=days;
                    }

                    if (min > 31) {
                        Log.d(LOG_TAG, "Booking New Appointment");
                        // Amount = "300";
                    } else if (min < 0) {
                        Log.d(LOG_TAG, "An appointment is already there ahead");
                        // Amount = "300";
                    }  else {
                        Log.d(LOG_TAG, "Booking Follow Up Appointment");
                        //Amount = "150";
                        //fetching follow up from firebase
                        try{
                            UserDb1 = FirebaseDatabase.getInstance().getReference().child("followup");
                            UserDb1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Amount = dataSnapshot.getValue().toString();
                                    Log.d(LOG_TAG,"FollowUp Amount : "+Amount);
                                    if(Amount==null)
                                        Amount="150";
                                    DoubleFollowUp=Double.parseDouble(Amount);
                                    PromoValue=""+deduction;
                                    FinalAmount=DoubleFollowUp;
                                    Log.d(LOG_TAG,"Final Amount Before Promo : "+FinalAmount);
                                    Log.d(LOG_TAG,"Type of Apt : "+aptType);
                                    PromoCode=PromoCodeET.getText().toString().toUpperCase();
                                    Progress2.dismiss();
                                    if(!PromoCode.trim().equals(""))
                                    {
                                        Progress2.setMessage("Checking Promocode");
                                        Progress2.setCancelable(false);
                                        Progress2.show();
                                        timerDelayRemoveDialog(30000,Progress2);
                                        UserDb2 = FirebaseDatabase.getInstance().getReference().child("promoCodes");
                                        UserDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                HashMap map=(HashMap) dataSnapshot.getValue();
                                                for (Object code: map.keySet()){
                                                    Log.d(LOG_TAG,"Promo Code : "+code.toString()+"Value : "+map.get(code.toString()).toString());
                                                    if(PromoCode.equals(code.toString())){
                                                        Progress2.dismiss();
                                                        deduction=Double.parseDouble(map.get(code.toString()).toString());
                                                        Log.d(LOG_TAG,"Deduction : "+deduction);
                                                        FinalAmount=DoubleFollowUp-deduction;
                                                        Log.d(LOG_TAG,"Promo Code : "+PromoCode+" applied!");
                                                        Toast.makeText(AppointmentActivity.this, "Promo Code : "+PromoCode+" applied!", Toast.LENGTH_SHORT).show();
                                                        Log.d(LOG_TAG,"Final Amount for CheckOut : "+FinalAmount);
                                                        PromoApplied=true;
                                                Answers.getInstance().logAddToCart(new AddToCartEvent()
                                                        .putItemPrice(BigDecimal.valueOf(FinalAmount))
                                                        .putCurrency(Currency.getInstance("INR"))
                                                        .putItemName(aptType));
                                                storeData();
                                                    }
                                                }
                                                if(!PromoApplied) {
                                                    Progress2.dismiss();
                                                    Log.d(LOG_TAG,"No Such PromoCode");
                                                    Toast.makeText(AppointmentActivity.this, "No Such Promo Code", Toast.LENGTH_LONG).show();
                                                    //PromoCodeET.selectAll();
                                                    PromoCodeET.setSelectAllOnFocus(true);
                                                    PromoCodeET.clearFocus();
                                                }



                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d(LOG_TAG,"Error PromoCode : "+databaseError.getMessage());
                                            }
                                        });
                                    }
                                    else
                                    {
                                        PromoApplied=false;
                                        Log.d(LOG_TAG,"Final Amount for CheckOut : "+FinalAmount);
                                Answers.getInstance().logAddToCart(new AddToCartEvent()
                                        .putItemPrice(BigDecimal.valueOf(DoubleAmount))
                                        .putCurrency(Currency.getInstance("INR"))
                                        .putItemName(aptType));
                                storeData();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(LOG_TAG,"Error FollowUp : "+databaseError.getMessage());
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Amount="150";
                            FinalAmount=Double.parseDouble(Amount);
                        }
                        aptType = "Follow Up Appointment";
                    }
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
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                                finish();
                                //Toast.makeText(getApplicationContext(), "Signing Out", Toast.LENGTH_LONG).show();

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
            case R.id.action_profile:
                startActivity(new Intent(AppointmentActivity.this, ProfileActivity.class));
                return true;
            case R.id.action_Report:
                startActivity(new Intent(AppointmentActivity.this, BugReportActivity.class));
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
        Progress2.setCancelable(false);
        Progress2.show();
        timerDelayRemoveDialog(30000,Progress2);
        rName=FbAuth.getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(rName)) {
            updateDisplayName(FirstName);

        }
        String registeredPhone="";
        try{
            registeredPhone=FbAuth.getCurrentUser().getPhoneNumber();
            if(registeredPhone==null)
            {
                registeredPhone="Not Verified";
            }
            else
            {
                registeredPhone=registeredPhone.substring(3);
            }
        }
        catch(Exception e)
        {
            Log.d(LOG_TAG,"Error : "+e.getMessage());
            registeredPhone="Not Verified";
        }

        final int intFinalAmount = (int) Math.round(FinalAmount);
        Log.d(LOG_TAG,"Checking out Rs "+intFinalAmount);
        Date = Day +"/"+(Month +1)+"/"+ Year;
        Data = new HashMap<>();
        Data.put("Name", FirstName);
        Data.put("Email", FbAuth.getCurrentUser().getEmail());
        Data.put("Phone", registeredPhone);
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
                //Toast.makeText(getApplicationContext(),"Stored Data",Toast.LENGTH_SHORT).show();
                Intent toConfirm = new Intent(AppointmentActivity.this, ConfirmActivity.class);
                toConfirm.putExtra("Name", FirstName);
                toConfirm.putExtra("Email", EmailET.getText().toString());
                toConfirm.putExtra("Phone", PhoneNumber);
                toConfirm.putExtra("Date", SelectedDate);
                toConfirm.putExtra("Amount",""+intFinalAmount);
                startActivity (toConfirm);
            }
        });
    }


    public void retrieve() {

        /**Function to Auto-fill information about a previously registered user.*/

        rName="";
        try {
            rName=FbAuth.getCurrentUser().getDisplayName();
        }
        catch (Exception e)
        {
            rName="";
            Log.d(LOG_TAG,"Error : "+e.getMessage());
        }
        if(FbAuth.getCurrentUser().getPhoneNumber()!=null) {
            PhoneNumber = FbAuth.getCurrentUser().getPhoneNumber().substring(3);
            PhoneET.setText(PhoneNumber);
        }
        else
            PhoneET.setText("");
        if(rName!=null)
        {
            NameET.setText(rName);
        }
        else
            NameET.setText("");
        Email=FbAuth.getCurrentUser().getEmail();

        EmailET.setText(Email);

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
            // Amount="300";
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
        final Query lastQuery = databaseReference.child(FbAuth.getCurrentUser().getUid()).orderByKey();
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap map=(HashMap) dataSnapshot.getValue();
                try
                {
                    for ( Object key : map.keySet() ) {

                        LatestDate=key.toString();

                        LatestDate=LatestDate.substring(0, LatestDate.indexOf(","));
                        LatestDate=LatestDate.replace("-","/");
                        dates.add(LatestDate);
                        newApt=true;

                        Log.d(LOG_TAG,"rName : "+rName);
                        Log.d(LOG_TAG,"Latest Date : "+LatestDate);
                        try{
                            if(LatestDate.equals("")||LatestDate==null)
                            {
                                //Amount ="300";
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
                    //Object key = map.keySet();
                    //LatestDate=key.toString();

                }
                catch(Exception e)
                {
                    Log.d(LOG_TAG,"Error inside getLatestAppt: "+e.getMessage());
                }
                //Do calculations here to find month diffrence

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

            if (isTaskRoot()) {
                Log.d(LOG_TAG, "No other Acitivites Exist");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(AppointmentActivity.this, R.style.AlertDialogCustom));
                builder.setCancelable(true);
                builder.setTitle("Exit App");
                builder.setMessage("Are you sure you want to Exit?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(LOG_TAG, "Exiting App");
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
            } else {
                Log.d(LOG_TAG, "Other Activities Exist");
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_DEL))
            Log.d(LOG_TAG,"Backspace Pressed");
        return super.onKeyDown(keyCode, event);
    }
    public boolean isConnected()
    {
        String command = "ping -c 1 google.com";
        Boolean isConnectedVar=false;
        try{

            isConnectedVar = (Runtime.getRuntime().exec (command).waitFor() == 0);
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG,"Exception : "+e.getMessage());
        }
        return isConnectedVar;
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try
                {
                    if(d.isShowing()) {
                        d.dismiss();
                        Toast.makeText(AppointmentActivity.this, "Taking Too Long Due To Connectivity Issues", Toast.LENGTH_LONG).show();
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

