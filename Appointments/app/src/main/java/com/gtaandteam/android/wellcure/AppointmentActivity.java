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
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class AppointmentActivity extends AppCompatActivity {

    /**
     * Data Structures
     */
    int Year, Month, Day;
    String FirstName, Email, PhoneNumber, Date;
    String rName, rEmail, rPhone, rDate; //retrieved files from database
    HashMap<String, String> Data;
    static String SelectedDate, TodaysDate;
    Boolean UserExists;

    /**
     * Views
     */
    EditText DateET, NameET, PhoneET, EmailET;
    TextView BookingTypeTV;
    Button BookAndPayBTN;
    Toolbar MyToolbar;

    /**
     * Firebase
     */
    FirebaseUser NewUser;
    private ProgressDialog Progress;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth FbAuth;
    DatabaseReference UserDb1, UserDb2, AppointmentDb;

    final String LOG_TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //Linking to views
        FbAuth = FirebaseAuth.getInstance();
        NewUser = FirebaseAuth.getInstance().getCurrentUser();
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
        UserExists = intent.getBooleanExtra("UserExists", false);
        Progress = new ProgressDialog(this);

        if (UserExists) {
            Log.d(LOG_TAG, "User Exists. Hence calling retrieve function");
            Progress.setMessage("Loading Existing Details of User");
            Progress.show();
            retrieve();
        } else {
            Log.d(LOG_TAG, "User Not Exists. Hence only setting EmailET and PhoneET");
            Progress.setMessage("Loading Details of New User");
            Progress.show();
            EmailET.setText(FbAuth.getCurrentUser().getEmail());
            PhoneET.setText(FbAuth.getCurrentUser().getPhoneNumber().substring(3));
            Progress.dismiss();
        }

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

        BookAndPayBTN.setVisibility(View.GONE); //In order to avoid invalid inputs
        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Year = cal.get(Calendar.YEAR);
                Month = cal.get(Calendar.MONTH);
                Day = cal.get(Calendar.DAY_OF_MONTH);
                TodaysDate = Day + "/" + (Month + 1) + "/" + Year;
                DatePickerDialog dialog = new DatePickerDialog(
                        AppointmentActivity.this, android.R.style.Theme_Holo_Dialog, mDateSetListener, Year, Month, Day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                DateET.setText(date);
                SelectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                Date endDate = new Date(year + "/" + (month + 1) + "/" + dayOfMonth); //Deprecation Warning [Date].
                Date startDate = Calendar.getInstance().getTime();
                long duration = endDate.getTime() - startDate.getTime();
                long diffday = duration / (24 * 60 * 60 * 1000) + 1;
                int days = (int) diffday - 1;
                if (days < 0) {
                    BookingTypeTV.setText("Appointment Date has already passed\nChoose A New Date");
                    BookAndPayBTN.setVisibility(View.INVISIBLE);
                } else {
                    BookingTypeTV.setText("Number of Days till Appointment: " + days);
                    BookAndPayBTN.setVisibility(View.VISIBLE);
                }

            }
        };


        BookAndPayBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("APP", "Clicked Submit");
                FirstName = NameET.getText().toString().trim();
                if (TextUtils.isEmpty(FirstName)) {
                    // is empty
                    Toast.makeText(AppointmentActivity.this, "Name Field Cannot Be Blank", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Name Field Is Blank");

                } else {
                    storeData();
                }

            }
        });


        setSupportActionBar(MyToolbar);
        // Get a support ActionBar corresponding to this MyToolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        //retrieve();

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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void signOut() {
        FbAuth.signOut();
    }

    public void storeData() {
        /**Stores user data in the database*/

        Log.v("APP", "Entered FUnction");

        //second_name=etSecondName.getText().toString();
        if (!UserExists) {
            updateDisplayName(FirstName);
        }
        Email = EmailET.getText().toString();
        PhoneNumber = PhoneET.getText().toString();
        Date = Day + "/" + (Month + 1) + "/" + Year;
        Data = new HashMap<>();
        Data.put("Name", FirstName);
        Data.put("Email", Email);
        Data.put("Phone", PhoneNumber);
        Data.put("LoginDate", Date);
        Log.v("APP", "Hashmap Done");
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("users");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).setValue(Data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //finish();
                //go to page which shows users details
                Log.v("APP", "Done Shit");
                Toast.makeText(getApplicationContext(), "Stored Data", Toast.LENGTH_SHORT).show();
                Intent toConfirm = new Intent(AppointmentActivity.this, ConfirmActivity.class);
                toConfirm.putExtra("Name", FirstName);
                toConfirm.putExtra("Email", Email);
                toConfirm.putExtra("Phone", PhoneNumber);
                toConfirm.putExtra("Date", SelectedDate);
                startActivity(toConfirm);
            }
        });
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


    public void retrieve() {

        /**Function to Auto-fill information about a previously registered user.*/

        UserDb2 = FirebaseDatabase.getInstance().getReference("users").child(FbAuth.getCurrentUser().getUid());
        UserDb2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String ss = dataSnapshot.getKey();
                switch (ss) {
                    case "Name":
                        rName = dataSnapshot.getValue().toString();
                        break;
                    case "Email":
                        rName = dataSnapshot.getValue().toString();
                        break;
                    case "Phone":
                        rPhone = dataSnapshot.getValue().toString();
                        break;
                    case "Date":
                        rDate = dataSnapshot.getValue().toString();
                        break;

                }
                NameET.setText("123");

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

    /*public void getLatestAppointment(){
        //Not tested yet....(Change to Direction.Acending if required)
        DatabaseReference databaseReference = Firebase.getInstance().getReference()
            .databaseReference.child("appointment").orderBy("date",Direction.DECENTING)
            .limit(1).addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String message = dataSnapshot.child("message").getValue().toString();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        //Handle possible errors.
    }
});
    }
*/
