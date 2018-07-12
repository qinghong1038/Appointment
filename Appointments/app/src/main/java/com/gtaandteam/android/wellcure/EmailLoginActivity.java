package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginActivity extends AppCompatActivity {

    EditText UsernameET,
            PasswordET;
    private ProgressDialog Progress;
    private FirebaseAuth FbAuth;
    Button RegisterButton,
            LoginButton;
    TextView textViewReg,forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forgotPasswordTextView = findViewById(R.id.forgot_password);

        final TextView OTPLogin =  findViewById(R.id.OTP_login);
        OTPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OTPLoginActivity.class));
                finish();

            }
        });

        FbAuth = FirebaseAuth.getInstance();
        if(FbAuth.getCurrentUser()!=null)
        {
            //user already logged in .. previous login credentials stored in PhoneNumber
            //then skip login and directly go to choosing doctor page
            finish();
            Intent i =new Intent(EmailLoginActivity.this, DoctorsActivity.class);
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

        UsernameET =(EditText)findViewById(R.id.username_editText);
        PasswordET =(EditText)findViewById(R.id.password_editText);
        Progress =new ProgressDialog(this);
        RegisterButton =findViewById(R.id.register_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(EmailLoginActivity.this,RegisterActivity.class));
            }
        });
        textViewReg = findViewById(R.id.textView);
        textViewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(EmailLoginActivity.this,RegisterActivity.class));
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = UsernameET.getText().toString().trim();
                if(emailAddress.equals(""))
                {
                    Toast.makeText(EmailLoginActivity.this, "Please Enter The EmailET ID and Try Again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("App", "EmailET sent.");
                                        Toast.makeText(EmailLoginActivity.this, "EmailET with Reset Link Sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

    }
    private void userLogin(){
        String email= UsernameET.getText().toString().trim();
        String pass= PasswordET.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            // is empty
            Toast.makeText(this,"Please Enter EmailET Id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            // is empty
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;

        }
        Progress.setMessage("Logging In");
        Progress.show();
        FbAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Progress.dismiss();
                        if(task.isSuccessful())
                        {
                            //user successfully logged in
                            //we start doctor activity here
                            Toast.makeText(EmailLoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i =new Intent(EmailLoginActivity.this, DoctorsActivity.class);
                            i.putExtra("loginMode",1);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(EmailLoginActivity.this,"Couldn't Login. Please Try Again..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
