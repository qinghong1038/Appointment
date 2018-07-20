package com.gtaandteam.android.wellcure;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorsActivity extends AppCompatActivity {

    /*
    TODO: Make a list to display multiple doctors
    todo: and then choose one to go to that doctor's page
     */

    /**Data Structures*/
    Boolean UserExists;

    /**Views*/
    Button AppointmentBTN;
    Toolbar MyToolbar;

    /**Firebase*/
    DatabaseReference UserDb1;
    FirebaseUser FbUser;
    private FirebaseAuth FbAuth;

    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        //Linking to views
        MyToolbar = findViewById(R.id.MyToolbar);
        AppointmentBTN = findViewById(R.id.get_appointment);


        FbAuth = FirebaseAuth.getInstance();
        FbUser = FbAuth.getCurrentUser();
        setSupportActionBar(MyToolbar);


        //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "" + FbUser.getPhoneNumber());


        Intent intent = getIntent();	//gives the reference to the destination intent
        final int loginMode = intent .getIntExtra("loginMode",0);	//loginMode is given in EmailLoginActivity and OTPLoginAcitivty


        AppointmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserExists();
                Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("UserExists", UserExists);
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
                Intent intent = new Intent(DoctorsActivity.this, PastActivity.class);
                intent.putExtra("Parent", LOG_TAG);
                startActivity(intent);
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

    private void checkUserExists() {
        Log.d(LOG_TAG, "Inside checkUser");
        try
        {
            UserDb1 = FirebaseDatabase.getInstance().getReference("users").child(FbAuth.getCurrentUser().getUid());
            UserDb1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String ss = dataSnapshot.getKey();
                    String name;
                    switch (ss) {
                        case "Name":
                            name = dataSnapshot.getValue().toString();
                            UserExists=true;
                            Log.d(LOG_TAG, "UserExists. Show Progress Bar after this");
                            break;

                    }
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
        catch (Exception e)
        {
            UserExists = false;
            Log.d(LOG_TAG, "User doesnt Exist. Dont Show Progress Bar after this");
        }
    }
}
