package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /*
    TODO: Homepage
    Doctor's Name and Photograph
    Book appointment button:
    Enter patient details
    Book & Pay button
    Displays pop up to confirm details
    Confirm button
    Takes you to Payment
    */
    Button LoginButton;
    EditText editTextUserName;
    EditText editTextPass;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    Button registerButton;
    TextView textViewReg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView OTPLogin =  findViewById(R.id.OTP_login);
        OTPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OTPLoginActivity.class));
                finish();

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            //user already logged in .. previous login credentials stored in phone
            //then skip login and directly go to choosing doctor page
            finish();
            Intent i =new Intent(MainActivity.this, DoctorsActivity.class);
            i.putExtra("loginMode",1);
            startActivity(i);
        }
        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });

        editTextUserName=(EditText)findViewById(R.id.username_editText);
        editTextPass=(EditText)findViewById(R.id.password_editText);
        progress=new ProgressDialog(this);
        registerButton=findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
        textViewReg = findViewById(R.id.textView);
        textViewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });


    }
    private void userLogin(){
        String email=editTextUserName.getText().toString().trim();
        String pass=editTextPass.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            // is empty
            Toast.makeText(this,"Please Enter Email Id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            // is empty
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;

        }
        progress.setMessage("Logging In");
        progress.show();
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();
                        if(task.isSuccessful())
                        {
                            //user successfully logged in
                            //we start doctor activity here
                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i =new Intent(MainActivity.this, DoctorsActivity.class);
                            i.putExtra("loginMode",1);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Couldn't Login. Please Try Again..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
