package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PastActivity extends AppCompatActivity {

    /**Views*/
    Toolbar MyToolbar;
    ListView AppointmentListView;
    TextView NoPast;
    private ProgressDialog Progress;


    /**FIrebase*/
    private FirebaseAuth FbAuth;
    DatabaseReference UserDb1;

    ArrayList<Appointment> appointments;
    AppointmentAdapter adapter;

    final String LOG_TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past);

        //Linking to views
        MyToolbar = findViewById(R.id.MyToolbar);
        AppointmentListView = findViewById(R.id.List);
        NoPast = findViewById(R.id.NoPastView);
        AppointmentListView.setVisibility(View.INVISIBLE);

        setSupportActionBar(MyToolbar);
        FbAuth = FirebaseAuth.getInstance();

        Progress =new ProgressDialog(this);
        Progress.setMessage("Fetching Past Appointments.");
        Progress.show();
        //TODO: Major FireBase changes/additions to be made in order to access past appointment Data from the particular ACCOUNT only
        //Following ArrayList is a temporary placeholder.

        /*appointments.add(new Appointment("Ritu Jain","Jan 26, 2018", R.drawable.doctor));
        appointments.add(new Appointment("Ritu Jain","Aug 15, 2018", R.drawable.doctor));
        appointments.add(new Appointment("Ritu Jain","October 2, 2018", R.drawable.doctor));
        appointments.add(new Appointment("Ritu Jain","July 28, 2018",R.drawable.doctor));
        appointments.add(new Appointment("Test for no Image"," Jan 01, 1970"));*/
        getAllAppointment();

        AppointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {


                Appointment currentAppointment = (Appointment)parent.getAdapter().getItem(position);

                Intent intent = new Intent(PastActivity.this, DetailsPopUp.class);
                intent.putExtra("Doctor", currentAppointment.getmDoctorName());
                intent.putExtra("DateTV", currentAppointment.getmDate());
                intent.putExtra("Fees", currentAppointment.getmFees());

                if(currentAppointment.hasImage()) {
                    intent.putExtra("DoctorImage", currentAppointment.getmDoctorImage());
                }
                else {
                    intent.putExtra("DoctorImage", R.drawable.headshot);
                }

                //TODO: DO NOT DELETE:
                //intent.putExtra("NameET", currentAppointment.getmPatientName());
                //intent.putExtra("Fees", currentAppointment.getmFees());
                startActivity(intent);
                }
        });
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
                // TODO: Handle user sign out.
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(PastActivity.this, R.style.AlertDialogCustom));
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
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }

    public void signOut() {
        FbAuth.signOut();
    }

    public void getAllAppointment(){
        appointments = new ArrayList<>();
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("appointmentDB");
        UserDb1.child(FbAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                Log.d(LOG_TAG, "Now entering for-loop" );

                for (DataSnapshot shot : snapshot.getChildren()) {
                    Appointment appointment= shot.getValue(Appointment.class);
                    appointments.add(appointment);
                    Log.d(LOG_TAG, "iteration: " + i++ );
                    //Log.d(LOG_TAG, "" + appointment.getmDoctorName().length() );

                    //add to array list
                    // Toast.makeText(Main2Activity.this, appointment.toString(), Toast.LENGTH_SHORT).show();

                }
                Log.d(LOG_TAG, "Now exiting for-loop" );
                adapter =new AppointmentAdapter(PastActivity.this, appointments);
                AppointmentListView.setAdapter(adapter);

                Log.d(LOG_TAG,  "Size of ArrayList: " + appointments.size());
                Progress.dismiss();
                if(appointments.size() > 0 )
                {
                    AppointmentListView.setVisibility(View.VISIBLE);
                    NoPast.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



}


