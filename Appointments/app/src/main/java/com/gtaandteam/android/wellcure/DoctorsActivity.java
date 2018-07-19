package com.gtaandteam.android.wellcure;

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
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.System.out;

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

        /*AuthCredential newCredential = EmailAuthProvider.getCredential("banana1@gmail.com","12345678");

        fbAuth.getCurrentUser().linkWithCredential(newCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("App", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //updateUI(user);
                        } else {
                            Log.w("App", "linkWithCredential:failure", task.getException());
                            //Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
                                //    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });*/
        
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

    private void checkUserExists()
    {
        UserDb1 = FirebaseDatabase.getInstance().getReference().child("users");
        try
        {
            UserDb1.child(FbAuth.getCurrentUser().getUid());
            UserExists = true;
        }
        catch (Exception e)
        {
            UserExists = false;
        }
    }
}
