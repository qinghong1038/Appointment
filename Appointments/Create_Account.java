package com.example.adityashenoyy.trial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adityashenoyy.iot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by ADITYA SHENOY Y on 6/11/2018.
 */

public class Create_Account extends AppCompatActivity {
    EditText etUser, etPassword, etFirstName, etSecondName, etEmail, etPhone;
    String user, password, first_name, second_name, email, phone;
    FirebaseAuth mauth;
    HashMap<String, String> data;
    DatabaseReference databaseObject;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);
        mauth=FirebaseAuth.getInstance();
        etUser=(EditText)findViewById(R.id.user);
        etPassword=(EditText)findViewById(R.id.password);
        etFirstName=(EditText)findViewById(R.id.first_name);
        etSecondName=(EditText)findViewById(R.id.second_name);
        etEmail=(EditText)findViewById(R.id.email_id);
        etPhone=(EditText)findViewById(R.id.phone_number);

    }
    //linked to onlick from xml //
    public void createAcc(View view){
        user= etUser.getText().toString();
        password= etPassword.getText().toString();
        first_name=etFirstName.getText().toString();
        second_name=etSecondName.getText().toString();
        email=etEmail.getText().toString();
        phone= etPhone.getText().toString();
        if (isDataFine())
            createAccount();
    }
// checks if data is fine
    private boolean isDataFine() {

        if (user.trim().length()<0 || !(user.contains("@")))
        {
            Toast.makeText(this, "nter proper user name, Like an email",Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.trim().length()<8 )
        {
            Toast.makeText(this, "Please enter atleast 8 char password",Toast.LENGTH_LONG).show();
            return false;
        }
        if (first_name.trim().length()<0 )
        {
            Toast.makeText(this, "Name cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (second_name.trim().length()<0)
        {
            Toast.makeText(this, "Second name cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (phone.trim().length()<10)
        {
            Toast.makeText(this, "Enter valid phone number",Toast.LENGTH_LONG).show();
            return false;
        }if (email.trim().length()<0 || !email.contains("@"))
        {
            Toast.makeText(this, "Enter proper emailid ",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //creates account with user id and password//
    private void createAccount() {

        final Task<AuthResult> authResultTask = mauth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //link phone number with account
                    AuthCredential credential = PhoneAuthProvider.getCredential(phone);
                    mauth.linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if linked succfully store thier data
                            if(task.isSuccessful())
                                storeData();
                            else
                            {

                                //remove user(Phone number linking failed)
                                FirebaseUser user=mauth.getCurrentUser();
                                user.delete();
                                Toast.makeText(getApplicationContext(), "Problem with phone this number already exsits\n you have to " +
                                        "refill you data", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Cannot Duplicate User\nPlease Try Again!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //STORE USER DATA IN DATABASE//
    public void storeData(){
        data= new HashMap<>();
        data.put("First Name", first_name);
        data.put("Second Name", second_name);
        data.put("Email", email);
        data.put("Phone", phone);
        databaseObject = FirebaseDatabase.getInstance().getReference().child("user");
        databaseObject.child(mauth.getCurrentUser().getUid()).setValue(data).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                //go to page which shows users details
            }
        });
    }
}

