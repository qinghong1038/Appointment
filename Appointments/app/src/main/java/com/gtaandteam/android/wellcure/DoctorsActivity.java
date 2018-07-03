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
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DoctorsActivity extends AppCompatActivity {

    /*
    TODO: Make a list to display multiple doctors
    todo: and then choose one to go to that doctor's page
     */

    Button AppointmentButton;
    Toolbar toolbar;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        Intent n3=getIntent();	//gives the ref to the destn intent
        final int i = n3.getIntExtra("loginMode",0);	//loginMode is given in MainActivity and OTPLoginAcitivty

        fbAuth = FirebaseAuth.getInstance();
        AppointmentButton = findViewById(R.id.get_appointment);
        AppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
                intent.putExtra("loginMode",i);
                startActivity(intent);


            }
        });

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


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
                        new ContextThemeWrapper(DoctorsActivity.this, R.style.AlertDialogCustom));
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
