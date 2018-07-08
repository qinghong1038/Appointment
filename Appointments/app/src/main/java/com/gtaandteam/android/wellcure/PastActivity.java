package com.gtaandteam.android.wellcure;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PastActivity extends AppCompatActivity {

    Toolbar toolbar;
    private FirebaseAuth fbAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        fbAuth = FirebaseAuth.getInstance();


        //TODO: Major FireBase changes/additions to be made in order to access past appointment data from the particular ACCOUNT only


        //Following ArrayList is a temporary placeholder.
        ArrayList<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment("Borkson Woofberg","Jan 26, 2018", R.drawable.dogtor));
        appointments.add(new Appointment("Dildo Lovegood","Aug 15, 2018", R.drawable.dildo));
        appointments.add(new Appointment("Cocksucker McGee","October 2, 2018", R.drawable.cocksucker));
        appointments.add(new Appointment("Meowette Furrington","July 28, 2018",R.drawable.cator));
        appointments.add(new Appointment("Test for no Image"," Jan 01, 1970"));



        ListView AppointmentListView = findViewById(R.id.List);

        AppointmentAdapter adapter =new AppointmentAdapter(PastActivity.this, appointments);


        AppointmentListView.setAdapter(adapter);

        AppointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {


                Appointment currentAppointment = (Appointment)parent.getAdapter().getItem(position);

                //TODO: Display a dialog-ish thing to display more info about appointment
                //startActivity(new Intent(PastActivity.this, DetailsPopUp.class));
                Intent intent = new Intent(PastActivity.this, DetailsPopUp.class);
                intent.putExtra("Doctor", currentAppointment.getmDoctorName());
                intent.putExtra("Date", currentAppointment.getmDate());
                if(currentAppointment.hasImage()) {
                    intent.putExtra("DoctorImage", currentAppointment.getmDoctorImage());
                }
                else {
                    intent.putExtra("DoctorImage", R.drawable.headshot);
                }

                //intent.putExtra("Name", currentAppointment.getmPatientName());
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
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        fbAuth.signOut();
    }



}


