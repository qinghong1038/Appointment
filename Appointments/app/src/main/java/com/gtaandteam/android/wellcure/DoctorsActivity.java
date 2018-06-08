package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DoctorsActivity extends AppCompatActivity {

    /*
    TODO: Make a list to display multiple doctors
    todo: and then choose one to go to that doctor's page
     */

    Button AppointmentButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        AppointmentButton = findViewById(R.id.get_appointment);
        AppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
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
                Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }
}
